/*
 * Copyright (C) 2022 ISNing
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
package com.yanzhenjie.andserver.server.async;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;

import com.yanzhenjie.andserver.AndServer;
import com.yanzhenjie.andserver.H2Server;
import com.yanzhenjie.andserver.delegate.CharCodingConfigDelegate;
import com.yanzhenjie.andserver.delegate.H2ConfigDelegate;
import com.yanzhenjie.andserver.delegate.Http1ConfigDelegate;
import com.yanzhenjie.andserver.delegate.IOReactorConfigDelegate;
import com.yanzhenjie.andserver.delegate.ListenerEndpoint;
import com.yanzhenjie.andserver.delegate.ListenerEndpointWrapper;
import com.yanzhenjie.andserver.http.URIScheme;
import com.yanzhenjie.andserver.util.Executors;

import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.hc.core5.function.Callback;
import org.apache.hc.core5.function.Decorator;
import org.apache.hc.core5.function.Supplier;
import org.apache.hc.core5.http.ConnectionReuseStrategy;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpConnection;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpRequestInterceptor;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.HttpResponseInterceptor;
import org.apache.hc.core5.http.config.CharCodingConfig;
import org.apache.hc.core5.http.config.Http1Config;
import org.apache.hc.core5.http.impl.Http1StreamListener;
import org.apache.hc.core5.http.impl.bootstrap.HttpAsyncServer;
import org.apache.hc.core5.http.io.HttpRequestHandler;
import org.apache.hc.core5.http.nio.AsyncServerExchangeHandler;
import org.apache.hc.core5.http.nio.ssl.TlsStrategy;
import org.apache.hc.core5.http.protocol.HttpProcessor;
import org.apache.hc.core5.http.protocol.HttpProcessorBuilder;
import org.apache.hc.core5.http2.HttpVersionPolicy;
import org.apache.hc.core5.http2.config.H2Config;
import org.apache.hc.core5.http2.frame.RawFrame;
import org.apache.hc.core5.http2.impl.H2Processors;
import org.apache.hc.core5.http2.impl.nio.H2StreamListener;
import org.apache.hc.core5.http2.impl.nio.bootstrap.H2ServerBootstrap;
import org.apache.hc.core5.io.CloseMode;
import org.apache.hc.core5.reactor.IOReactorConfig;
import org.apache.hc.core5.reactor.IOSession;
import org.apache.hc.core5.reactor.IOSessionListener;
import org.apache.hc.core5.util.Timeout;

import java.net.SocketAddress;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by Zhenjie Yan on 3/7/20.
 */
public abstract class BasicH2Server<T extends BasicH2Server.Builder<T, ?>> implements H2Server {

    @Nullable
    protected final String mCanonicalHostName;
    @Nullable
    protected final IOReactorConfig mIOReactorConfig;
    @Nullable
    protected final Http1Config mHttp1Config;
    @Nullable
    protected final H2Config mH2Config;
    @Nullable
    protected final CharCodingConfig mCharCodingConfig;
    @Nullable
    protected final HttpVersionPolicy mVersionPolicy;
    @Nullable
    protected final HttpProcessor mHttpProcessor;
    @Nullable
    protected final TlsStrategy mTlsStrategy;
    @Nullable
    protected final Timeout mTlsHandshakeTimeout;
    @Nullable
    protected final Decorator<IOSession> mIOSessionDecorator;
    @Nullable
    protected final Callback<Exception> mExceptionCallback;
    @Nullable
    protected final IOSessionListener mIOSessionListener;
    @Nullable
    protected final Http1StreamListener mHttp1StreamListener;
    @Nullable
    protected final H2StreamListener mH2StreamListener;
    protected final ServerListener mListener;
    protected boolean isRunning;
    private HttpAsyncServer mHttpServer;

    BasicH2Server(T builder) {
        this.mCanonicalHostName = builder.canonicalHostName;
        this.mIOReactorConfig = builder.ioReactorConfig;
        this.mHttp1Config = builder.http1Config;
        this.mH2Config = builder.h2Config;
        this.mCharCodingConfig = builder.charCodingConfig;
        this.mVersionPolicy = builder.versionPolicy;
        this.mTlsStrategy = builder.tlsStrategy;
        this.mTlsHandshakeTimeout = builder.tlsHandshakeTimeout;
        this.mHttpProcessor = builder.httpProcessor;
        this.mIOSessionDecorator = builder.ioSessionDecorator;
        this.mExceptionCallback = builder.exceptionCallback;
        this.mIOSessionListener = builder.ioSessionListener;
        this.mHttp1StreamListener = builder.http1StreamListener;
        this.mH2StreamListener = builder.h2StreamListener;
        this.mListener = builder.listener;
    }

    public void initializeServer() {
        try {
            Pair<IOReactorConfig, Boolean> ioReactorConfig = requestIOReactorConfig();
            Pair<Http1Config, Boolean> http1Config = requestHttp1Config();
            Pair<H2Config, Boolean> h2Config = requestH2Config();
            Pair<CharCodingConfig, Boolean> charCodingConfig = requestCharCodingConfig();
            Pair<HttpVersionPolicy, Boolean> versionPolicy = requestVersionPolicy();
            Pair<TlsStrategy, Boolean> tlsStrategy = requestTlsStrategy();
            Pair<Timeout, Boolean> tlsHandshakeTimeout = requestTlsHandshakeTimeout();
            HttpProcessor httpProcessor = requestHttpProcessor();
            HttpProcessorBuilder httpProcessorBuilder = H2Processors.customServer(AndServer.INFO);
            if (httpProcessor != null) {
                httpProcessorBuilder.add((HttpRequestInterceptor) httpProcessor);
                httpProcessorBuilder.add((HttpResponseInterceptor) httpProcessor);
            }
            if (mHttpProcessor != null) {
                httpProcessorBuilder.add((HttpRequestInterceptor) mHttpProcessor);
                httpProcessorBuilder.add((HttpResponseInterceptor) mHttpProcessor);
            }

            H2ServerBootstrap bootstrap = H2ServerBootstrap.bootstrap()
                    .setCanonicalHostName(mCanonicalHostName)
                    .setIOReactorConfig(ioReactorConfig.getRight() && mIOReactorConfig != null ? mIOReactorConfig : ioReactorConfig.getLeft())
                    .setHttp1Config(http1Config.getRight() && mHttp1Config != null ? mHttp1Config : http1Config.getLeft())
                    .setH2Config(h2Config.getRight() && mH2Config != null ? mH2Config : h2Config.getLeft())
                    .setCharset(charCodingConfig.getRight() && mCharCodingConfig != null ? mCharCodingConfig : charCodingConfig.getLeft())
                    .setVersionPolicy(versionPolicy.getRight() && mVersionPolicy != null ? mVersionPolicy : versionPolicy.getLeft())
                    .setTlsStrategy(tlsStrategy.getRight() && mTlsStrategy != null ? mTlsStrategy : tlsStrategy.getLeft())
                    .setHandshakeTimeout(tlsHandshakeTimeout.getRight() && mTlsHandshakeTimeout != null ? mTlsHandshakeTimeout : tlsHandshakeTimeout.getLeft())
                    .setHttpProcessor(httpProcessorBuilder.build())
                    .setIOSessionDecorator(object -> {
                        IOSession ret = object;
                        Decorator<IOSession> decorator = requestIOSessionDecorator();
                        if (decorator != null)
                            ret = decorator.decorate(ret);
                        if (mIOSessionDecorator != null)
                            ret = mIOSessionDecorator.decorate(ret);
                        return ret;
                    })
                    .setExceptionCallback(object -> {
                        Callback<Exception> cb = requestExceptionCallback();
                        if (cb != null) cb.execute(object);
                        if (mExceptionCallback != null)
                            mExceptionCallback.execute(object);
                        mListener.onException(object);
                    })
                    .setIOSessionListener(new IOSessionListener() {
                        private final IOSessionListener listener = requestIOSessionListener();

                        @Override
                        public void connected(IOSession session) {
                            if (listener != null)
                                listener.connected(session);
                            if (mIOSessionListener != null)
                                mIOSessionListener.connected(session);
                        }

                        @Override
                        public void startTls(IOSession session) {
                            if (listener != null)
                                listener.startTls(session);
                            if (mIOSessionListener != null)
                                mIOSessionListener.startTls(session);
                        }

                        @Override
                        public void inputReady(IOSession session) {
                            if (listener != null)
                                listener.inputReady(session);
                            if (mIOSessionListener != null)
                                mIOSessionListener.inputReady(session);
                        }

                        @Override
                        public void outputReady(IOSession session) {
                            if (listener != null)
                                listener.outputReady(session);
                            if (mIOSessionListener != null)
                                mIOSessionListener.outputReady(session);
                        }

                        @Override
                        public void timeout(IOSession session) {
                            if (listener != null)
                                listener.timeout(session);
                            if (mIOSessionListener != null)
                                mIOSessionListener.timeout(session);
                        }

                        @Override
                        public void exception(IOSession session, Exception ex) {
                            if (listener != null)
                                listener.exception(session, ex);
                            if (mIOSessionListener != null)
                                mIOSessionListener.exception(session, ex);
                            mListener.onException(ex);
                        }

                        @Override
                        public void disconnected(IOSession session) {
                            if (listener != null)
                                listener.disconnected(session);
                            if (mIOSessionListener != null)
                                mIOSessionListener.disconnected(session);
                        }
                    })
                    .setStreamListener(new Http1StreamListener() {
                        private final Http1StreamListener listener = requestHttp1StreamListener();

                        @Override
                        public void onRequestHead(HttpConnection connection, HttpRequest request) {
                            if (listener != null)
                                listener.onRequestHead(connection, request);
                            if (mHttp1StreamListener != null)
                                mHttp1StreamListener.onRequestHead(connection, request);
                        }

                        @Override
                        public void onResponseHead(HttpConnection connection, HttpResponse response) {
                            if (listener != null)
                                listener.onResponseHead(connection, response);
                            if (mHttp1StreamListener != null)
                                mHttp1StreamListener.onResponseHead(connection, response);
                        }

                        @Override
                        public void onExchangeComplete(HttpConnection connection, boolean keepAlive) {
                            if (listener != null)
                                listener.onExchangeComplete(connection, keepAlive);
                            if (mHttp1StreamListener != null)
                                mHttp1StreamListener.onExchangeComplete(connection, keepAlive);
                        }
                    })
                    .setStreamListener(new H2StreamListener() {
                        private final H2StreamListener listener = requestH2StreamListener();

                        @Override
                        public void onHeaderInput(HttpConnection connection, int streamId, List<? extends Header> headers) {
                            if (listener != null)
                                listener.onHeaderInput(connection, streamId, headers);
                            if (mH2StreamListener != null)
                                mH2StreamListener.onHeaderInput(connection, streamId, headers);
                        }

                        @Override
                        public void onHeaderOutput(HttpConnection connection, int streamId, List<? extends Header> headers) {
                            if (listener != null)
                                listener.onHeaderOutput(connection, streamId, headers);
                            if (mH2StreamListener != null)
                                mH2StreamListener.onHeaderOutput(connection, streamId, headers);
                        }

                        @Override
                        public void onFrameInput(HttpConnection connection, int streamId, RawFrame frame) {
                            if (listener != null)
                                listener.onFrameInput(connection, streamId, frame);
                            if (mH2StreamListener != null)
                                mH2StreamListener.onFrameInput(connection, streamId, frame);
                        }

                        @Override
                        public void onFrameOutput(HttpConnection connection, int streamId, RawFrame frame) {
                            if (listener != null)
                                listener.onFrameOutput(connection, streamId, frame);
                            if (mH2StreamListener != null)
                                mH2StreamListener.onFrameOutput(connection, streamId, frame);
                        }

                        @Override
                        public void onInputFlowControl(HttpConnection connection, int streamId, int delta, int actualSize) {
                            if (listener != null)
                                listener.onInputFlowControl(connection, streamId, delta, actualSize);
                            if (mH2StreamListener != null)
                                mH2StreamListener.onInputFlowControl(connection, streamId, delta, actualSize);
                        }

                        @Override
                        public void onOutputFlowControl(HttpConnection connection, int streamId, int delta, int actualSize) {
                            if (listener != null)
                                listener.onOutputFlowControl(connection, streamId, delta, actualSize);
                            if (mH2StreamListener != null)
                                mH2StreamListener.onOutputFlowControl(connection, streamId, delta, actualSize);
                        }
                    });

            // Register handlers
            for (ImmutableTriple<String, String, Supplier<AsyncServerExchangeHandler>> triple : requestHandlers()) {
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
            initializeServer();

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
                Executors.getInstance().post(() -> {
                    if (mListener != null) {
                        mListener.onStopped();
                    }
                });
            }
            isRunning = false;
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
     * Right: Handler supplier (NonNull)
     */
    @NonNull
    protected abstract Collection<ImmutableTriple<String, String, Supplier<AsyncServerExchangeHandler>>> requestHandlers();

    /**
     * Assigns {@link IOReactorConfig} instance.
     * Pair definition:
     * Left: IO reactor config (Nullable)
     * Right: Whether overridable (NonNull)
     */
    @NonNull
    protected Pair<IOReactorConfig, Boolean> requestIOReactorConfig() {
        return Pair.of(null, true);
    }

    /**
     * Assigns {@link Http1Config} instance.
     * Pair definition:
     * Left: Http config (Nullable)
     * Right: Whether overridable (NonNull)
     */
    @NonNull
    protected Pair<Http1Config, Boolean> requestHttp1Config() {
        return Pair.of(null, true);
    }

    /**
     * Assigns {@link H2Config} instance.
     * Pair definition:
     * Left: Http config (Nullable)
     * Right: Whether overridable (NonNull)
     */
    @NonNull
    protected Pair<H2Config, Boolean> requestH2Config() {
        return Pair.of(null, true);
    }

    /**
     * Assigns {@link CharCodingConfig} instance.
     * Pair definition:
     * Left: Char coding config (Nullable)
     * Right: Whether overridable (NonNull)
     */
    @NonNull
    protected Pair<CharCodingConfig, Boolean> requestCharCodingConfig() {
        return Pair.of(null, true);
    }

    /**
     * Assigns {@link HttpVersionPolicy} instance.
     * Pair definition:
     * Left: Http version policy (Nullable)
     * Right: Whether overridable (NonNull)
     */
    @NonNull
    protected Pair<HttpVersionPolicy, Boolean> requestVersionPolicy() {
        return Pair.of(null, true);
    }

    /**
     * Assigns {@link TlsStrategy} instance.
     * Pair definition:
     * Left: Tls strategy (Nullable)
     * Right: Whether overridable (NonNull)
     */
    @NonNull
    protected Pair<TlsStrategy, Boolean> requestTlsStrategy() {
        return Pair.of(null, true);
    }

    /**
     * Assigns {@link Timeout} instance.
     * Pair definition:
     * Left: Tls strategy (Nullable)
     * Right: Whether overridable (NonNull)
     */
    @NonNull
    protected Pair<Timeout, Boolean> requestTlsHandshakeTimeout() {
        return Pair.of(null, true);
    }

    /**
     * Assigns {@link HttpProcessor} instance.
     */
    @Nullable
    protected HttpProcessor requestHttpProcessor() {
        return null;
    }

    /**
     * Assigns {@link Decorator<IOSession>} instance.
     */
    @Nullable
    protected Decorator<IOSession> requestIOSessionDecorator() {
        return null;
    }

    /**
     * Assigns {@link Http1StreamListener} instance.
     */
    @Nullable
    protected Http1StreamListener requestHttp1StreamListener() {
        return null;
    }


    /**
     * Assigns {@link H2StreamListener} instance.
     */
    @Nullable
    protected H2StreamListener requestH2StreamListener() {
        return null;
    }

    /**
     * Assigns {@link Callback<Exception>} instance.
     */
    @Nullable
    protected Callback<Exception> requestExceptionCallback() {
        return null;
    }

    /**
     * Assigns {@link IOSessionListener} instance.
     */
    @Nullable
    protected IOSessionListener requestIOSessionListener() {
        return null;
    }

    @Override
    public Future<ListenerEndpoint> listen(SocketAddress address, URIScheme scheme, Object attachment, com.yanzhenjie.andserver.delegate.FutureCallback<ListenerEndpoint> callback) {
        return ListenerEndpointWrapper.wrap(mHttpServer.listen(address, scheme.getScheme(), attachment, ListenerEndpointWrapper.wrap(callback.wrapped())));
    }

    @Override
    public Future<ListenerEndpoint> listen(SocketAddress address, URIScheme scheme, com.yanzhenjie.andserver.delegate.FutureCallback<ListenerEndpoint> callback) {
        return ListenerEndpointWrapper.wrap(mHttpServer.listen(address, scheme, ListenerEndpointWrapper.wrap(callback.wrapped())));
    }

    @Override
    public Future<ListenerEndpoint> listen(SocketAddress address, URIScheme scheme) {
        return ListenerEndpointWrapper.wrap(mHttpServer.listen(address, scheme.getScheme()));
    }

    @Override
    public Future<ListenerEndpoint> listen(SocketAddress address, Object attachment, com.yanzhenjie.andserver.delegate.FutureCallback<ListenerEndpoint> callback) {
        return ListenerEndpointWrapper.wrap(mHttpServer.listen(address, attachment, ListenerEndpointWrapper.wrap(callback.wrapped())));
    }

    @Override
    public Set<ListenerEndpoint> getEndPoints() {
        if (isRunning) {
            Set<org.apache.hc.core5.reactor.ListenerEndpoint> endpoints = mHttpServer.getEndpoints();
            Set<ListenerEndpoint> endpointsWrapped = new HashSet<>();
            for (org.apache.hc.core5.reactor.ListenerEndpoint endpoint : endpoints)
                endpointsWrapped.add(new ListenerEndpointWrapper(endpoint));
            return endpointsWrapped;
        }
        throw new IllegalStateException("The server has not been started yet.");
    }

    @Nullable
    public String getCanonicalHostName() {
        return mCanonicalHostName;
    }

    @Nullable
    public IOReactorConfigDelegate getIOReactorConfig() {
        return new IOReactorConfigDelegate(mIOReactorConfig);
    }

    @Nullable
    public Http1ConfigDelegate getHttp1Config() {
        return new Http1ConfigDelegate(mHttp1Config);
    }

    @Nullable
    public CharCodingConfigDelegate getCharCodingConfig() {
        return new CharCodingConfigDelegate(mCharCodingConfig);
    }

    public HttpProcessor getHttpProcessor() {
        return mHttpProcessor;
    }

    @Nullable
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public TlsStrategy getTlsStrategy() {
        return mTlsStrategy;
    }

    @Nullable
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public Timeout getTlsHandshakeTimeout() {
        return mTlsHandshakeTimeout;
    }

    @Nullable
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public Decorator<IOSession> getIOSessionDecorator() {
        return mIOSessionDecorator;
    }

    @Nullable
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public Callback<Exception> getExceptionCallback() {
        return mExceptionCallback;
    }

    @Nullable
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public IOSessionListener getIOSessionListener() {
        return mIOSessionListener;
    }

    @Nullable
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public Http1StreamListener getStreamListener() {
        return mHttp1StreamListener;
    }

    public ServerListener getListener() {
        return mListener;
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public HttpAsyncServer getHttpServer() {
        return mHttpServer;
    }

    protected abstract static class Builder<T extends Builder<T, S>, S extends BasicH2Server<T>>
            implements H2Server.Builder<T, S> {

        String canonicalHostName;
        IOReactorConfig ioReactorConfig;
        HttpVersionPolicy versionPolicy;
        Http1Config http1Config;
        H2Config h2Config;
        CharCodingConfig charCodingConfig;
        HttpProcessor httpProcessor;
        ConnectionReuseStrategy connectionReuseStrategy;
        TlsStrategy tlsStrategy;
        Timeout tlsHandshakeTimeout;
        Decorator<IOSession> ioSessionDecorator;
        Callback<Exception> exceptionCallback;
        IOSessionListener ioSessionListener;
        Http1StreamListener http1StreamListener;
        H2StreamListener h2StreamListener;
        ServerListener listener;

        Builder() {
        }

        @Override
        public T setCanonicalHostName(String canonicalHostName) {
            this.canonicalHostName = canonicalHostName;
            //noinspection unchecked
            return (T) this;
        }

        @Override
        public T setIOReactorConfig(IOReactorConfigDelegate ioReactorConfig) {
            this.ioReactorConfig = ioReactorConfig.getIOReactorConfig();
            //noinspection unchecked
            return (T) this;
        }

        @Override
        public T setVersionPolicy(com.yanzhenjie.andserver.http2.HttpVersionPolicy versionPolicy) {
            this.versionPolicy = versionPolicy.wrapped();
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
        public T setH2Config(H2ConfigDelegate h2Config) {
            this.h2Config = h2Config.getH2Config();
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
        public T setTlsStrategy(com.yanzhenjie.andserver.delegate.ssl.TlsStrategy tlsStrategy) {
            this.tlsStrategy = tlsStrategy.wrapped();
            //noinspection unchecked
            return (T) this;
        }

        @Override
        public T setTlsHandshakeTimeout(long duration, TimeUnit timeUnit) {
            this.tlsHandshakeTimeout = Timeout.of(duration, timeUnit);
            //noinspection unchecked
            return (T) this;
        }

        @Override
        @RestrictTo(RestrictTo.Scope.LIBRARY)
        public T setIOSessionDecorator(Decorator<IOSession> ioSessionDecorator) {
            this.ioSessionDecorator = ioSessionDecorator;
            //noinspection unchecked
            return (T) this;
        }

        @Override
        public T setExceptionCallback(com.yanzhenjie.andserver.delegate.Callback<Exception> exceptionCallback) {
            this.exceptionCallback = exceptionCallback.wrapped();
            //noinspection unchecked
            return (T) this;
        }

        @Override
        @RestrictTo(RestrictTo.Scope.LIBRARY)
        public T setIOSessionListener(IOSessionListener sessionListener) {
            this.ioSessionListener = sessionListener;
            //noinspection unchecked
            return (T) this;
        }

        @Override
        public T setStreamListener(com.yanzhenjie.andserver.delegate.Http1StreamListener streamListener) {
            this.http1StreamListener = streamListener.wrapped();
            //noinspection unchecked
            return (T) this;
        }

        @Override
        public T setStreamListener(com.yanzhenjie.andserver.delegate.H2StreamListener streamListener) {
            this.h2StreamListener = streamListener.wrapped();
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