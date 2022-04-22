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
import androidx.annotation.Nullable;

import com.yanzhenjie.andserver.SSLSocketInitializer;
import com.yanzhenjie.andserver.Server;
import com.yanzhenjie.andserver.util.Executors;

import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.hc.core5.function.Callback;
import org.apache.hc.core5.http.ExceptionListener;
import org.apache.hc.core5.http.impl.Http1StreamListener;
import org.apache.hc.core5.http.impl.bootstrap.HttpServer;
import org.apache.hc.core5.http.impl.bootstrap.ServerBootstrap;
import org.apache.hc.core5.http.io.HttpRequestHandler;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.io.CloseMode;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;

import java.net.InetAddress;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;

/**
 * Created by Zhenjie Yan on 3/7/20.
 */
public abstract class BasicServer<T extends BasicServer.Builder<?, ?>> implements Server {

    static final int BUFFER = 8 * 1024;

    protected final InetAddress mInetAddress;
    protected final String mCanonicalHostName;
    protected final int mPort;
    protected final Timeout mTimeout;
    protected final ServerSocketFactory mSocketFactory;
    protected final SSLContext mSSLContext;
    protected final SSLSocketInitializer mSSLSocketInitializer;
    protected final Server.ServerListener mListener;

    private HttpServer mHttpServer;
    protected boolean isRunning;

    BasicServer(T builder) {
        this.mInetAddress = builder.inetAddress;
        this.mCanonicalHostName = builder.canonicalHostName;
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

        Executors.getInstance().execute(() -> {
            try {
                ServerBootstrap bootstrap = ServerBootstrap.bootstrap()
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
                                        .setSoLinger(TimeValue.ZERO_MILLISECONDS)
                                        .build()
                        )
                        .setLocalAddress(mInetAddress)
                        .setCanonicalHostName(mCanonicalHostName)
                        .setListenerPort(mPort)
                        .setSslContext(mSSLContext)
                        .setSslSetupHandler(new SSLSetup(mSSLSocketInitializer));
                // Register handlers
                for (ImmutableTriple<String, String, HttpRequestHandler> triple : requestHandlers()) {
                    if (triple == null) {
                        continue;
                    }
                    if (triple.left == null) {
                        bootstrap.register(triple.middle, triple.right);
                    } else {
                        bootstrap.registerVirtual(triple.left, triple.middle, triple.right);
                    }
                }
                // Register StreamListeners
                Http1StreamListener http1StreamListener = requestHttp1StreamListener();
                if (http1StreamListener != null) {
                    bootstrap.setStreamListener(http1StreamListener);
                }
                // Register ExceptionListeners
                ExceptionListener exceptionListener = requestExceptionListener();
                if (exceptionListener != null) {
                    bootstrap.setExceptionListener(exceptionListener);
                }
                mHttpServer = bootstrap.create();

                mHttpServer.start();
                isRunning = true;

                Executors.getInstance().post(() -> {
                    if (mListener != null) {
                        mListener.onStarted();
                    }
                });
                Runtime.getRuntime().addShutdownHook(new Thread() {
                    @Override
                    public void run() {
                        mHttpServer.close(CloseMode.GRACEFUL);
                    }
                });
            } catch (final Exception e) {
                Executors.getInstance().post(() -> {
                    if (mListener != null) {
                        mListener.onException(e);
                    }
                });
            }
        });
    }

    /**
     * Assigns {@link HttpRequestHandler} instances.
     * @return A {@link Collection} of {@link ImmutableTriple}
     * Triple definition:
     * Left: Host name (Nullable)
     * (null -> canonical hostname / localhost loopback)
     * (nonnull -> virtual hostname)
     * Middle: uri pattern (NonNull)
     * Right: Handler (NonNull)
     */
    @NonNull
    protected abstract Collection<ImmutableTriple<String, String, HttpRequestHandler>> requestHandlers();

    /**
     * Assigns {@link Http1StreamListener} instance.
     */
    @Nullable
    protected Http1StreamListener requestHttp1StreamListener() {
        return null;
    }

    /**
     * Assigns {@link ExceptionListener} instance.
     */
    @Nullable
    protected ExceptionListener requestExceptionListener(){
        return null;
    }

    /**
     * Quit the server.
     */
    @Override
    public void shutdown() {
        if (!isRunning) {
            return;
        }

        Executors.getInstance().execute(() -> {
            if (mHttpServer != null) {
                mHttpServer.close(CloseMode.GRACEFUL);
                isRunning = false;
                Executors.getInstance().post(() -> {
                    if (mListener != null) {
                        mListener.onStopped();
                    }
                });
            }
        });
    }

    private static final class SSLSetup implements Callback<SSLParameters> {

        private final SSLSocketInitializer mInitializer;

        public SSLSetup(@NonNull SSLSocketInitializer initializer) {
            this.mInitializer = initializer;
        }

        @Override
        public void execute(SSLParameters object) {
            mInitializer.onCreated(object);
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

    protected abstract static class Builder<T extends Builder<?, ?>, S extends BasicServer<?>> {

        InetAddress inetAddress;
        String canonicalHostName;
        int port;
        Timeout timeout;
        ServerSocketFactory mSocketFactory;
        SSLContext sslContext;
        SSLSocketInitializer mSSLSocketInitializer;
        Server.ServerListener listener;

        Builder() {
        }

        public T inetAddress(InetAddress inetAddress) {
            this.inetAddress = inetAddress;
            //noinspection unchecked
            return (T) this;
        }

        public T canonicalHostName(String canonicalHostName) {
            this.canonicalHostName = canonicalHostName;
            //noinspection unchecked
            return (T) this;
        }

        public T port(int port) {
            this.port = port;
            //noinspection unchecked
            return (T) this;
        }

        public T timeout(int timeout, TimeUnit timeUnit) {
            long timeoutMs = timeUnit.toMillis(timeout);
            this.timeout = Timeout.ofMicroseconds((int) Math.min(timeoutMs, Integer.MAX_VALUE));
            //noinspection unchecked
            return (T) this;
        }

        public T serverSocketFactory(ServerSocketFactory factory) {
            this.mSocketFactory = factory;
            //noinspection unchecked
            return (T) this;
        }

        public T sslContext(SSLContext sslContext) {
            this.sslContext = sslContext;
            //noinspection unchecked
            return (T) this;
        }

        public T sslSocketInitializer(SSLSocketInitializer initializer) {
            this.mSSLSocketInitializer = initializer;
            //noinspection unchecked
            return (T) this;
        }

        public T listener(Server.ServerListener listener) {
            this.listener = listener;
            //noinspection unchecked
            return (T) this;
        }

        public abstract S build();
    }
}