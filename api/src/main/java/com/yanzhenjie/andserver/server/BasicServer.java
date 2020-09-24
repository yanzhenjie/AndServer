/*
 * Copyright 2020 Zhenjie Yan.
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
package com.yanzhenjie.andserver.server;

import androidx.annotation.NonNull;

import com.yanzhenjie.andserver.AndServer;
import com.yanzhenjie.andserver.SSLSocketInitializer;
import com.yanzhenjie.andserver.Server;
import com.yanzhenjie.andserver.util.Executors;

import org.apache.httpcore.ExceptionLogger;
import org.apache.httpcore.config.SocketConfig;
import org.apache.httpcore.impl.bootstrap.HttpServer;
import org.apache.httpcore.impl.bootstrap.SSLServerSetupHandler;
import org.apache.httpcore.impl.bootstrap.ServerBootstrap;
import org.apache.httpcore.protocol.HttpRequestHandler;

import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLServerSocket;

/**
 * Created by Zhenjie Yan on 3/7/20.
 */
public abstract class BasicServer<T extends BasicServer.Builder> implements Server {

    static final int BUFFER = 8 * 1024;

    protected final InetAddress mInetAddress;
    protected final int mPort;
    protected final int mTimeout;
    protected final ServerSocketFactory mSocketFactory;
    protected final SSLContext mSSLContext;
    protected final SSLSocketInitializer mSSLSocketInitializer;
    protected final Server.ServerListener mListener;

    private HttpServer mHttpServer;
    protected boolean isRunning;

    BasicServer(T builder) {
        this.mInetAddress = builder.inetAddress;
        this.mPort = builder.port;
        this.mTimeout = builder.timeout;
        this.mSocketFactory = builder.mSocketFactory;
        this.mSSLContext = builder.sslContext;
        this.mSSLSocketInitializer = builder.mSSLSocketInitializer;
        this.mListener = builder.listener;
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public void startup() {
        if (isRunning) {
            return;
        }

        Executors.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    mHttpServer = ServerBootstrap.bootstrap()
                        .setServerSocketFactory(mSocketFactory)
                        .setSocketConfig(
                            SocketConfig.custom()
                                .setSoKeepAlive(true)
                                .setSoReuseAddress(true)
                                .setTcpNoDelay(true)
                                .setSoTimeout(mTimeout)
                                .setBacklogSize(BUFFER)
                                .setRcvBufSize(BUFFER)
                                .setSndBufSize(BUFFER)
                                .setSoLinger(0)
                                .build()
                        )
                        .setLocalAddress(mInetAddress)
                        .setListenerPort(mPort)
                        .setSslContext(mSSLContext)
                        .setSslSetupHandler(new SSLSetup(mSSLSocketInitializer))
                        .setServerInfo(AndServer.INFO)
                        .registerHandler("*", requestHandler())
                        .setExceptionLogger(ExceptionLogger.NO_OP)
                        .create();

                    mHttpServer.start();
                    isRunning = true;

                    Executors.getInstance().post(new Runnable() {
                        @Override
                        public void run() {
                            if (mListener != null) {
                                mListener.onStarted();
                            }
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
                            if (mListener != null) {
                                mListener.onException(e);
                            }
                        }
                    });
                }
            }
        });
    }

    /**
     * Assigns {@link HttpRequestHandler} instance.
     */
    protected abstract HttpRequestHandler requestHandler();

    /**
     * Quit the server.
     */
    @Override
    public void shutdown() {
        if (!isRunning) {
            return;
        }

        Executors.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                if (mHttpServer != null) {
                    mHttpServer.shutdown(3, TimeUnit.SECONDS);
                    isRunning = false;
                    Executors.getInstance().post(new Runnable() {
                        @Override
                        public void run() {
                            if (mListener != null) {
                                mListener.onStopped();
                            }
                        }
                    });
                }
            }
        });
    }

    private static final class SSLSetup implements SSLServerSetupHandler {

        private final SSLSocketInitializer mInitializer;

        public SSLSetup(@NonNull SSLSocketInitializer initializer) {
            this.mInitializer = initializer;
        }

        @Override
        public void initialize(SSLServerSocket socket) throws SSLException {
            mInitializer.onCreated(socket);
        }
    }

    @Override
    public InetAddress getInetAddress() {
        if (isRunning) {
            return mHttpServer.getInetAddress();
        }
        throw new IllegalStateException("The server has not been started yet.");
    }

    @Override
    public int getPort() {
        if (isRunning) {
            return mHttpServer.getLocalPort();
        }
        throw new IllegalStateException("The server has not been started yet.");
    }

    protected abstract static class Builder<T extends Builder, S extends BasicServer> {

        InetAddress inetAddress;
        int port;
        int timeout;
        ServerSocketFactory mSocketFactory;
        SSLContext sslContext;
        SSLSocketInitializer mSSLSocketInitializer;
        Server.ServerListener listener;

        Builder() {
        }

        public T inetAddress(InetAddress inetAddress) {
            this.inetAddress = inetAddress;
            return (T) this;
        }

        public T port(int port) {
            this.port = port;
            return (T) this;
        }

        public T timeout(int timeout, TimeUnit timeUnit) {
            long timeoutMs = timeUnit.toMillis(timeout);
            this.timeout = (int) Math.min(timeoutMs, Integer.MAX_VALUE);
            return (T) this;
        }

        public T serverSocketFactory(ServerSocketFactory factory) {
            this.mSocketFactory = factory;
            return (T) this;
        }

        public T sslContext(SSLContext sslContext) {
            this.sslContext = sslContext;
            return (T) this;
        }

        public T sslSocketInitializer(SSLSocketInitializer initializer) {
            this.mSSLSocketInitializer = initializer;
            return (T) this;
        }

        public T listener(Server.ServerListener listener) {
            this.listener = listener;
            return (T) this;
        }

        public abstract S build();
    }
}