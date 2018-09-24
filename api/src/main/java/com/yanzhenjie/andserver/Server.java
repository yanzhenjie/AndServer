/*
 * Copyright © 2018 YanZhenjie.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yanzhenjie.andserver;

import android.support.annotation.NonNull;

import com.yanzhenjie.andserver.util.Executors;

import org.apache.commons.io.Charsets;
import org.apache.httpcore.ExceptionLogger;
import org.apache.httpcore.config.ConnectionConfig;
import org.apache.httpcore.config.SocketConfig;
import org.apache.httpcore.impl.bootstrap.HttpServer;
import org.apache.httpcore.impl.bootstrap.SSLServerSetupHandler;
import org.apache.httpcore.impl.bootstrap.ServerBootstrap;

import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLServerSocket;

/**
 * Created by YanZhenjie on 2018/9/10.
 */
public class Server {

    public static Builder newBuilder() {
        return new Builder();
    }

    private final InetAddress mInetAddress;
    private final int mPort;
    private final int mTimeout;
    private final SSLContext mSSLContext;
    private final SSLInitializer mSSLInitializer;
    private final ServerListener mListener;

    private HttpServer mHttpServer;
    private boolean isRunning;

    private Server(Builder builder) {
        this.mInetAddress = builder.mInetAddress;
        this.mPort = builder.mPort;
        this.mTimeout = builder.mTimeout;
        this.mSSLContext = builder.mSSLContext;
        this.mSSLInitializer = builder.mSSLInitializer;
        this.mListener = builder.mListener;
    }

    /**
     * Server running status.
     *
     * @return return true, not return false.
     */
    public boolean isRunning() {
        return isRunning;
    }

    /**
     * Start the server.
     */
    public void startup() {
        if (isRunning) return;

        Executors.getInstance().submit(new Runnable() {
            @Override
            public void run() {
                DispatcherHandler handler = new DispatcherHandler(AndServer.getContext());
                ComponentRegister register = new ComponentRegister(AndServer.getContext());
                register.register(handler);

                mHttpServer = ServerBootstrap.bootstrap()
                    .setSocketConfig(SocketConfig.custom()
                        .setSoKeepAlive(true)
                        .setSoReuseAddress(true)
                        .setSoTimeout(mTimeout)
                        .setTcpNoDelay(true)
                        .build())
                    .setConnectionConfig(
                        ConnectionConfig.custom().setBufferSize(4 * 1024).setCharset(Charsets.UTF_8).build())
                    .setLocalAddress(mInetAddress)
                    .setListenerPort(mPort)
                    .setSslContext(mSSLContext)
                    .setSslSetupHandler(new SSLInitializerWrapper(mSSLInitializer))
                    .setServerInfo("AndServer/2.0.0")
                    .registerHandler("*", handler)
                    .setExceptionLogger(ExceptionLogger.NO_OP)
                    .create();
                try {
                    isRunning = true;
                    mHttpServer.start();

                    Executors.getInstance().post(new Runnable() {
                        @Override
                        public void run() {
                            if (mListener != null) mListener.onStarted();
                        }
                    });
                    Runtime.getRuntime().addShutdownHook(new Thread() {
                        @Override
                        public void run() {
                            mHttpServer.shutdown(3, TimeUnit.SECONDS);
                        }
                    });
                } catch (final Exception e) {
                    Executors.getInstance().post(new Runnable() {
                        @Override
                        public void run() {
                            if (mListener != null) mListener.onException(e);
                        }
                    });
                }
            }
        });
    }

    /**
     * Quit the server.
     */
    public void shutdown() {
        if (!isRunning) return;

        Executors.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                if (mHttpServer != null) mHttpServer.shutdown(3, TimeUnit.MINUTES);
                Executors.getInstance().post(new Runnable() {
                    @Override
                    public void run() {
                        if (mListener != null) mListener.onStopped();
                    }
                });
            }
        });
    }

    private final class SSLInitializerWrapper implements SSLServerSetupHandler {

        private final SSLInitializer mSSLSocketInitializer;

        public SSLInitializerWrapper(@NonNull SSLInitializer initializer) {
            this.mSSLSocketInitializer = initializer;
        }

        public void initialize(SSLServerSocket socket) throws SSLException {
            mSSLSocketInitializer.onCreated(socket);
        }
    }

    /**
     * Get the network address.
     *
     * @throws IllegalStateException if the server is not started, an IllegalStateException is thrown.
     */
    public InetAddress getInetAddress() {
        if (isRunning) return mHttpServer.getInetAddress();
        throw new IllegalStateException("The server has not been started yet.");
    }

    public static class Builder {

        private InetAddress mInetAddress;
        private int mPort;
        private int mTimeout;
        private SSLContext mSSLContext;
        private SSLInitializer mSSLInitializer;
        private ServerListener mListener;

        private Builder() {
        }

        /**
         * Specified server need to monitor the ip address.
         */
        public Builder inetAddress(InetAddress inetAddress) {
            this.mInetAddress = inetAddress;
            return this;
        }

        /**
         * Specify the port on which the server listens.
         */
        public Builder port(int port) {
            this.mPort = port;
            return this;
        }

        /**
         * Connection and response timeout.
         */
        public Builder timeout(int timeout, TimeUnit timeUnit) {
            long timeoutMs = timeUnit.toMillis(timeout);
            this.mTimeout = (int)Math.min(timeoutMs, Integer.MAX_VALUE);
            return this;
        }

        /**
         * Setting up the server is based on the SSL protocol.
         */
        public Builder sslContext(SSLContext sslContext) {
            this.mSSLContext = sslContext;
            return this;
        }

        /**
         * Set SSLServerSocket's initializer.
         */
        public Builder sslSocketInitializer(SSLInitializer initializer) {
            this.mSSLInitializer = initializer;
            return this;
        }

        /**
         * Set the server listener.
         */
        public Builder listener(ServerListener listener) {
            this.mListener = listener;
            return this;
        }

        /**
         * Create a server.
         */
        public Server build() {
            return new Server(this);
        }
    }

    public interface ServerListener {

        /**
         * When the server is started.
         */
        void onStarted();

        /**
         * When the server stops running.
         */
        void onStopped();

        /**
         * An error occurred while starting the server.
         */
        void onException(Exception e);
    }
}