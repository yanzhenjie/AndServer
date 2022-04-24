/*
 * Copyright (C) 2020 Zhenjie Yan
 *               2022 ISNing
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
package com.yanzhenjie.andserver.server.classic;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;

import com.yanzhenjie.andserver.AndServer;
import com.yanzhenjie.andserver.ClassicServer;
import com.yanzhenjie.andserver.delegate.CharCodingConfigDelegate;
import com.yanzhenjie.andserver.delegate.Http1ConfigDelegate;
import com.yanzhenjie.andserver.delegate.SocketConfigDelegate;
import com.yanzhenjie.andserver.util.Executors;

import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.hc.core5.function.Callback;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ConnectionReuseStrategy;
import org.apache.hc.core5.http.ExceptionListener;
import org.apache.hc.core5.http.HttpConnection;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpRequestInterceptor;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.HttpResponseFactory;
import org.apache.hc.core5.http.HttpResponseInterceptor;
import org.apache.hc.core5.http.config.CharCodingConfig;
import org.apache.hc.core5.http.config.Http1Config;
import org.apache.hc.core5.http.impl.Http1StreamListener;
import org.apache.hc.core5.http.impl.HttpProcessors;
import org.apache.hc.core5.http.impl.bootstrap.HttpServer;
import org.apache.hc.core5.http.impl.bootstrap.ServerBootstrap;
import org.apache.hc.core5.http.impl.io.DefaultBHttpServerConnection;
import org.apache.hc.core5.http.io.HttpConnectionFactory;
import org.apache.hc.core5.http.io.HttpRequestHandler;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.http.protocol.HttpProcessor;
import org.apache.hc.core5.http.protocol.HttpProcessorBuilder;
import org.apache.hc.core5.io.CloseMode;

import java.net.InetAddress;
import java.util.Collection;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;

/**
 * Created by Zhenjie Yan on 3/7/20.
 */
public abstract class BasicClassicServer<T extends BasicClassicServer.Builder<T, ?>> implements ClassicServer {

    protected final String mCanonicalHostName;
    protected final int mListenerPort;
    protected final InetAddress mLocalAddress;
    protected final SocketConfig mSocketConfig;
    protected final Http1Config mHttp1Config;
    protected final CharCodingConfig mCharCodingConfig;
    protected final HttpProcessor mHttpProcessor;
    protected final ConnectionReuseStrategy mConnectionReuseStrategy;
    protected final HttpResponseFactory<ClassicHttpResponse> mResponseFactory;
    protected final ServerSocketFactory mServerSocketFactory;
    protected final SSLContext mSslContext;
    protected final Callback<SSLParameters> mSslSetupHandler;
    protected final HttpConnectionFactory<? extends DefaultBHttpServerConnection> mConnectionFactory;
    protected final ExceptionListener mExceptionListener;
    protected final Http1StreamListener mStreamListener;
    protected final ClassicServer.ServerListener mListener;
    protected boolean isRunning;
    private HttpServer mHttpServer;

    BasicClassicServer(T builder) {
        this.mCanonicalHostName = builder.canonicalHostName;
        this.mListenerPort = builder.listenerPort;
        this.mLocalAddress = builder.localAddress;
        this.mSocketConfig = builder.socketConfig;
        this.mHttp1Config = builder.http1Config;
        this.mCharCodingConfig = builder.charCodingConfig;
        this.mHttpProcessor = builder.httpProcessor;
        this.mConnectionReuseStrategy = builder.connectionReuseStrategy;
        this.mResponseFactory = builder.responseFactory;
        this.mServerSocketFactory = builder.serverSocketFactory;
        this.mSslContext = builder.sslContext;
        this.mSslSetupHandler = builder.sslSetupHandler;
        this.mConnectionFactory = builder.connectionFactory;
        this.mExceptionListener = builder.exceptionListener;
        this.mStreamListener = builder.streamListener;
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
                Pair<SocketConfig, Boolean> socketConfig = requestSocketConfig();
                Pair<Http1Config, Boolean> http1Config = requestHttp1Config();
                Pair<CharCodingConfig, Boolean> charCodingConfig = requestCharCodingConfig();
                HttpProcessor httpProcessor = requestHttpProcessor();
                Pair<ConnectionReuseStrategy, Boolean> connectionReuseStrategy = requestConnectionReuseStrategy();
                Pair<HttpResponseFactory<ClassicHttpResponse>, Boolean> responseFactory = requestResponseFactory();
                Pair<ServerSocketFactory, Boolean> serverSocketFactory = requestServerSocketFactory();
                Pair<SSLContext, Boolean> sslContext = requestSslContext();
                Pair<HttpConnectionFactory<? extends DefaultBHttpServerConnection>, Boolean> connectionFactory = requestConnectionFactory();

                HttpProcessorBuilder httpProcessorBuilder = HttpProcessors.customServer(AndServer.INFO);
                if (httpProcessor != null) {
                    httpProcessorBuilder.add((HttpRequestInterceptor) httpProcessor);
                    httpProcessorBuilder.add((HttpResponseInterceptor) httpProcessor);
                }
                if (mHttpProcessor != null) {
                    httpProcessorBuilder.add((HttpRequestInterceptor) mHttpProcessor);
                    httpProcessorBuilder.add((HttpResponseInterceptor) mHttpProcessor);
                }

                ServerBootstrap bootstrap = ServerBootstrap.bootstrap()
                        .setCanonicalHostName(mCanonicalHostName)
                        .setListenerPort(mListenerPort)
                        .setLocalAddress(mLocalAddress)
                        .setSocketConfig(socketConfig.getRight() && mSocketConfig != null ? mSocketConfig : socketConfig.getLeft())
                        .setHttp1Config(http1Config.getRight() && mHttp1Config != null ? mHttp1Config : http1Config.getLeft())
                        .setCharCodingConfig(charCodingConfig.getRight() && mCharCodingConfig != null ? mCharCodingConfig : charCodingConfig.getLeft())
                        .setHttpProcessor(httpProcessorBuilder.build())
                        .setConnectionReuseStrategy(connectionReuseStrategy.getRight() && mConnectionReuseStrategy != null ? mConnectionReuseStrategy : connectionReuseStrategy.getLeft())
                        .setResponseFactory(responseFactory.getRight() && mResponseFactory != null ? mResponseFactory : responseFactory.getLeft())
                        .setServerSocketFactory(serverSocketFactory.getRight() && mServerSocketFactory != null ? mServerSocketFactory : serverSocketFactory.getLeft())
                        .setSslContext(sslContext.getRight() && mSslContext != null ? mSslContext : sslContext.getLeft())
                        .setSslSetupHandler(object -> {
                            Callback<SSLParameters> cb = requestSslSetupHandler();
                            if (cb != null) cb.execute(object);
                            if (mSslSetupHandler != null)
                                mSslSetupHandler.execute(object);
                        })
                        .setConnectionFactory(connectionFactory.getRight() && mConnectionFactory != null ? mConnectionFactory : connectionFactory.getLeft())
                        .setExceptionListener(new ExceptionListener() {
                            @Override
                            public void onError(Exception ex) {
                                ExceptionListener exceptionListener = requestExceptionListener();
                                if (exceptionListener != null) exceptionListener.onError(ex);
                                if (mExceptionListener != null)
                                    mExceptionListener.onError(ex);
                                mListener.onException(ex);
                            }

                            @Override
                            public void onError(HttpConnection connection, Exception ex) {
                                ExceptionListener exceptionListener = requestExceptionListener();
                                if (exceptionListener != null)
                                    exceptionListener.onError(connection, ex);
                                if (mExceptionListener != null)
                                    mExceptionListener.onError(connection, ex);
                                mListener.onException(ex);
                            }
                        })
                        .setStreamListener(new Http1StreamListener() {
                            private final Http1StreamListener listener = requestHttp1StreamListener();

                            @Override
                            public void onRequestHead(HttpConnection connection, HttpRequest request) {
                                if (listener != null)
                                    listener.onRequestHead(connection, request);
                                if (mStreamListener != null)
                                    mStreamListener.onRequestHead(connection, request);
                            }

                            @Override
                            public void onResponseHead(HttpConnection connection, HttpResponse response) {
                                if (listener != null)
                                    listener.onResponseHead(connection, response);
                                if (mStreamListener != null)
                                    mStreamListener.onResponseHead(connection, response);
                            }

                            @Override
                            public void onExchangeComplete(HttpConnection connection, boolean keepAlive) {
                                if (listener != null)
                                    listener.onExchangeComplete(connection, keepAlive);
                                if (mStreamListener != null)
                                    mStreamListener.onExchangeComplete(connection, keepAlive);
                            }
                        });

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
                mHttpServer = bootstrap.create();

                mHttpServer.start();

                Runnable startupHook = requestStartupHook();
                if (startupHook != null)
                    startupHook.run();

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
                        Runnable shutdownHook = requestShutdownHook();
                        if (shutdownHook != null)
                            shutdownHook.run();
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

                Runnable shutdownHook = requestShutdownHook();
                if (shutdownHook != null)
                    shutdownHook.run();

                isRunning = false;
                Executors.getInstance().post(() -> {
                    if (mListener != null) {
                        mListener.onStopped();
                    }
                });
            }
        });
    }


    /**
     * Startup Hook Thread
     *
     * @return A {@link Runnable} should be executed after server startup
     */
    @Nullable
    protected Runnable requestStartupHook() {
        return null;
    }

    /**
     * Shutdown Hook Thread
     *
     * @return A {@link Runnable} should be executed after server stopped
     */
    @Nullable
    protected Runnable requestShutdownHook() {
        return null;
    }

    /**
     * Assigns {@link HttpRequestHandler} instances.
     *
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
     * Assigns {@link SocketConfig} instance.
     * Pair definition:
     * Left: Socket config (Nullable)
     * Right: Whether overridable (NonNull)
     */
    @NonNull
    protected Pair<SocketConfig, Boolean> requestSocketConfig() {
        return Pair.of(null, false);
    }

    /**
     * Assigns {@link Http1Config} instance.
     * Pair definition:
     * Left: Http config (Nullable)
     * Right: Whether overridable (NonNull)
     */
    @NonNull
    protected Pair<Http1Config, Boolean> requestHttp1Config() {
        return Pair.of(null, false);
    }

    /**
     * Assigns {@link CharCodingConfig} instance.
     * Pair definition:
     * Left: Char coding config (Nullable)
     * Right: Whether overridable (NonNull)
     */
    @NonNull
    protected Pair<CharCodingConfig, Boolean> requestCharCodingConfig() {
        return Pair.of(null, false);
    }

    /**
     * Assigns {@link HttpProcessor} instance.
     */
    @Nullable
    protected HttpProcessor requestHttpProcessor() {
        return null;
    }

    /**
     * Assigns {@link ConnectionReuseStrategy} instance.
     * Pair definition:
     * Left: Connection reuse strategy (Nullable)
     * Right: Whether overridable (NonNull)
     */
    @NonNull
    protected Pair<ConnectionReuseStrategy, Boolean> requestConnectionReuseStrategy() {
        return Pair.of(null, true);
    }

    /**
     * Assigns {@link HttpResponseFactory<ClassicHttpResponse>} instance.
     * Pair definition:
     * Left: Http response factory (Nullable)
     * Right: Whether overridable (NonNull)
     */
    @NonNull
    protected Pair<HttpResponseFactory<ClassicHttpResponse>, Boolean> requestResponseFactory() {
        return Pair.of(null, true);
    }

    /**
     * Assigns {@link ServerSocketFactory} instance.
     * Pair definition:
     * Left: Server socket factory (Nullable)
     * Right: Whether overridable (NonNull)
     */
    @NonNull
    protected Pair<ServerSocketFactory, Boolean> requestServerSocketFactory() {
        return Pair.of(null, true);
    }

    /**
     * Assigns {@link SSLContext} instance.
     * Pair definition:
     * Left: SSLContext (Nullable)
     * Right: Whether overridable (NonNull)
     */
    @NonNull
    protected Pair<SSLContext, Boolean> requestSslContext() {
        return Pair.of(null, true);
    }

    /**
     * Assigns {@link Callback<SSLParameters>} instance.
     */
    @Nullable
    protected Callback<SSLParameters> requestSslSetupHandler() {
        return null;
    }

    /**
     * Assigns {@link HttpConnectionFactory} instance.
     * Pair definition:
     * Left: Http connection factory (Nullable)
     * Right: Whether overridable (NonNull)
     */
    @NonNull
    protected Pair<HttpConnectionFactory<? extends DefaultBHttpServerConnection>, Boolean> requestConnectionFactory() {
        return Pair.of(null, true);
    }

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
    protected ExceptionListener requestExceptionListener() {
        return null;
    }

    @Override
    public InetAddress getLocalAddress() {
        if (isRunning) {
            return mHttpServer.getInetAddress();
        }
        throw new IllegalStateException("The server has not been started yet.");
    }

    @Override
    public int getLocalPort() {
        if (isRunning) {
            return mHttpServer.getLocalPort();
        }
        throw new IllegalStateException("The server has not been started yet.");
    }

    public String getCanonicalHostName() {
        return mCanonicalHostName;
    }

    @Nullable
    public SocketConfigDelegate getSocketConfig() {
        return new SocketConfigDelegate(mSocketConfig);
    }

    @Nullable
    public Http1ConfigDelegate getHttp1Config() {
        return new Http1ConfigDelegate(mHttp1Config);
    }

    @Nullable
    public CharCodingConfigDelegate getCharCodingConfig() {
        return new CharCodingConfigDelegate(mCharCodingConfig);
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public HttpProcessor getHttpProcessor() {
        return mHttpProcessor;
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public ConnectionReuseStrategy getConnectionReuseStrategy() {
        return mConnectionReuseStrategy;
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public HttpResponseFactory<ClassicHttpResponse> getResponseFactory() {
        return mResponseFactory;
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public ServerSocketFactory getServerSocketFactory() {
        return mServerSocketFactory;
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public SSLContext getSslContext() {
        return mSslContext;
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public Callback<SSLParameters> getSslSetupHandler() {
        return mSslSetupHandler;
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public HttpConnectionFactory<? extends DefaultBHttpServerConnection> getConnectionFactory() {
        return mConnectionFactory;
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public ExceptionListener getExceptionListener() {
        return mExceptionListener;
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public Http1StreamListener getStreamListener() {
        return mStreamListener;
    }

    public ServerListener getListener() {
        return mListener;
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public HttpServer getHttpServer() {
        return mHttpServer;
    }

    protected abstract static class Builder<T extends Builder<T, S>, S extends BasicClassicServer<T>> implements ClassicServer.Builder<T, S> {

        String canonicalHostName;
        int listenerPort;
        InetAddress localAddress;
        SocketConfig socketConfig;
        Http1Config http1Config;
        CharCodingConfig charCodingConfig;
        HttpProcessor httpProcessor;
        ConnectionReuseStrategy connectionReuseStrategy;
        HttpResponseFactory<ClassicHttpResponse> responseFactory;
        ServerSocketFactory serverSocketFactory;
        SSLContext sslContext;
        Callback<SSLParameters> sslSetupHandler;
        HttpConnectionFactory<? extends DefaultBHttpServerConnection> connectionFactory;
        ExceptionListener exceptionListener;
        Http1StreamListener streamListener;
        ClassicServer.ServerListener listener;

        Builder() {
        }

        @Override
        public T setCanonicalHostName(String canonicalHostName) {
            this.canonicalHostName = canonicalHostName;
            //noinspection unchecked
            return (T) this;
        }

        @Override
        public T setListenerPort(int listenerPort) {
            this.listenerPort = listenerPort;
            //noinspection unchecked
            return (T) this;
        }

        @Override
        public T setLocalAddress(InetAddress localAddress) {
            this.localAddress = localAddress;
            //noinspection unchecked
            return (T) this;
        }

        @Override
        public T setSocketConfig(SocketConfigDelegate socketConfig) {
            this.socketConfig = socketConfig.getSocketConfig();
            //noinspection unchecked
            return (T) this;
        }

        @Override
        public T setHttp1Config(Http1ConfigDelegate http1Config) {
            this.http1Config = http1Config.getHttp1Config();
            //noinspection unchecked
            return (T) this;
        }

        @Override
        public T setCharCodingConfig(CharCodingConfigDelegate charCodingConfig) {
            this.charCodingConfig = charCodingConfig.getCharCodingConfig();
            //noinspection unchecked
            return (T) this;
        }

        @Override
        public T setHttpProcessor(com.yanzhenjie.andserver.delegate.HttpProcessor httpProcessor) {
            this.httpProcessor = httpProcessor.wrapped();
            //noinspection unchecked
            return (T) this;
        }

        @Override
        public T setConnectionReuseStrategy(com.yanzhenjie.andserver.delegate.ConnectionReuseStrategy connectionReuseStrategy) {
            this.connectionReuseStrategy = connectionReuseStrategy.wrapped();
            //noinspection unchecked
            return (T) this;
        }

        @Override
        @RestrictTo(RestrictTo.Scope.LIBRARY)
        public T setResponseFactory(HttpResponseFactory<ClassicHttpResponse> responseFactory) {
            this.responseFactory = responseFactory;
            //noinspection unchecked
            return (T) this;
        }

        @Override
        public T setServerSocketFactory(ServerSocketFactory serverSocketFactory) {
            this.serverSocketFactory = serverSocketFactory;
            //noinspection unchecked
            return (T) this;
        }

        @Override
        public T setSslContext(SSLContext sslContext) {
            this.sslContext = sslContext;
            //noinspection unchecked
            return (T) this;
        }

        @Override
        public T setSslSetupHandler(com.yanzhenjie.andserver.delegate.Callback<SSLParameters> sslSetupHandler) {
            this.sslSetupHandler = sslSetupHandler.wrapped();
            //noinspection unchecked
            return (T) this;
        }

        @Override
        @RestrictTo(RestrictTo.Scope.LIBRARY)
        public T setConnectionFactory(HttpConnectionFactory<? extends DefaultBHttpServerConnection> connectionFactory) {
            this.connectionFactory = connectionFactory;
            //noinspection unchecked
            return (T) this;
        }

        @Override
        public T setExceptionListener(com.yanzhenjie.andserver.delegate.ExceptionListener exceptionListener) {
            this.exceptionListener = exceptionListener.wrapped();
            //noinspection unchecked
            return (T) this;
        }

        @Override
        public T setStreamListener(com.yanzhenjie.andserver.delegate.Http1StreamListener streamListener) {
            this.streamListener = streamListener.wrapped();
            //noinspection unchecked
            return (T) this;
        }

        @Override
        public T setListener(ServerListener listener) {
            this.listener = listener;
            //noinspection unchecked
            return (T) this;
        }
    }
}