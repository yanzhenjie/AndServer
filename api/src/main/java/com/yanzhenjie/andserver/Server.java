/*
 * Copyright © 2018 Zhenjie Yan.
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

import android.content.Context;

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

import androidx.annotation.NonNull;

/**
 * Created by Zhenjie Yan on 2018/9/10.
 */
public class Server {

    public static Builder newBuilder(Context context, @NonNull String group) {
        return new Builder(context, group);
    }

    private final Context mContext;
    private final String mGroup;
    private final InetAddress mInetAddress;
    private final int mPort;
    private final int mTimeout;
    private final SSLContext mSSLContext;
    private final SSLInitializer mSSLInitializer;
    private final ServerListener mListener;

    private HttpServer mHttpServer;
    private boolean isRunning;

    private Server(Builder builder) {
        this.mContext = builder.context;
        this.mGroup = builder.group;
        this.mInetAddress = builder.inetAddress;
        this.mPort = builder.port;
        this.mTimeout = builder.timeout;
        this.mSSLContext = builder.sslContext;
        this.mSSLInitializer = builder.sslInitializer;
        this.mListener = builder.listener;
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

        Executors.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                DispatcherHandler handler = new DispatcherHandler(mContext);
                ComponentRegister register = new ComponentRegister(mContext);
                register.register(handler, mGroup);

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
                if (mHttpServer != null) {
                    mHttpServer.shutdown(3, TimeUnit.MINUTES);
                    isRunning = false;
                    Executors.getInstance().post(new Runnable() {
                        @Override
                        public void run() {
                            if (mListener != null) mListener.onStopped();
                        }
                    });
                }
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

        private Context context;
        private String group;
        private InetAddress inetAddress;
        private int port;
        private int timeout;
        private SSLContext sslContext;
        private SSLInitializer sslInitializer;
        private ServerListener listener;

        private Builder(Context context, String group) {
            this.context = context;
            this.group = group;
        }

        /**
         * Specified server need to monitor the ip address.
         */
        public Builder inetAddress(InetAddress inetAddress) {
            this.inetAddress = inetAddress;
            return this;
        }

        /**
         * Specify the port on which the server listens.
         */
        public Builder port(int port) {
            this.port = port;
            return this;
        }

        /**
         * Connection and response timeout.
         */
        public Builder timeout(int timeout, TimeUnit timeUnit) {
            long timeoutMs = timeUnit.toMillis(timeout);
            this.timeout = (int)Math.min(timeoutMs, Integer.MAX_VALUE);
            return this;
        }

        /**
         * Setting up the server is based on the SSL protocol.
         */
        public Builder sslContext(SSLContext sslContext) {
            this.sslContext = sslContext;
            return this;
        }

        /**
         * Set SSLServerSocket's initializer.
         */
        public Builder sslSocketInitializer(SSLInitializer initializer) {
            this.sslInitializer = initializer;
            return this;
        }

        /**
         * Set the server listener.
         */
        public Builder listener(ServerListener listener) {
            this.listener = listener;
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