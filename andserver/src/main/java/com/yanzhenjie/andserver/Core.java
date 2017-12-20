/*
 * Copyright © Yan Zhenjie. All Rights Reserved
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

import com.yanzhenjie.andserver.exception.resolver.ExceptionResolver;
import com.yanzhenjie.andserver.interceptor.Interceptor;
import com.yanzhenjie.andserver.ssl.SSLSocketInitializer;
import com.yanzhenjie.andserver.util.Executors;
import com.yanzhenjie.andserver.website.WebSite;

import org.apache.httpcore.ExceptionLogger;
import org.apache.httpcore.config.ConnectionConfig;
import org.apache.httpcore.config.SocketConfig;
import org.apache.httpcore.impl.bootstrap.HttpServer;
import org.apache.httpcore.impl.bootstrap.ServerBootstrap;

import java.net.InetAddress;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;

/**
 * Created by Yan Zhenjie on 2017/3/13.
 */
final class Core extends Thread {

    static Builder newBuilder() {
        return new Builder();
    }

    private final InetAddress mInetAddress;
    private final int mPort;
    private final int mTimeout;
    private final SSLContext mSSLContext;
    private final SSLSocketInitializer mSSLSocketInitializer;
    private final Interceptor mInterceptor;
    private final ExceptionResolver mExceptionResolver;
    private final Map<String, RequestHandler> mRequestHandlerMap;
    private final WebSite mWebSite;
    private final StartupListener mListener;

    private HttpServer mHttpServer;
    private boolean isRunning;

    private Core(Builder builder) {
        this.mInetAddress = builder.mInetAddress;
        this.mPort = builder.mPort;
        this.mTimeout = builder.mTimeout;
        this.mSSLContext = builder.mSSLContext;
        this.mSSLSocketInitializer = builder.mSSLSocketInitializer;
        this.mInterceptor = builder.mInterceptor;
        this.mExceptionResolver = builder.mExceptionResolver;
        this.mRequestHandlerMap = builder.mRequestHandlerMap;
        this.mWebSite = builder.mWebSite;
        this.mListener = builder.mListener;
    }

    @Override
    public void run() {
        DispatchRequestHandler handler = new DispatchRequestHandler();
        handler.setInterceptor(mInterceptor);
        handler.setExceptionResolver(mExceptionResolver);
        handler.setWebSite(mWebSite);

        if (mRequestHandlerMap != null && mRequestHandlerMap.size() > 0) {
            for (Map.Entry<String, RequestHandler> handlerEntry : mRequestHandlerMap.entrySet()) {
                String path = handlerEntry.getKey();
                RequestHandler requestHandler = handlerEntry.getValue();
                handler.registerRequestHandler(path, requestHandler);
            }
        }

        mHttpServer = ServerBootstrap.bootstrap()
                .setSocketConfig(
                        SocketConfig.custom()
                                .setSoKeepAlive(true)
                                .setSoReuseAddress(false)
                                .setSoTimeout(mTimeout)
                                .setTcpNoDelay(false)
                                .build()
                )
                .setConnectionConfig(
                        ConnectionConfig.custom()
                                .setBufferSize(4 * 1024)
                                .setCharset(Charset.defaultCharset())
                                .build()
                )
                .setLocalAddress(mInetAddress)
                .setListenerPort(mPort)
                .setSslContext(mSSLContext)
                .setSslSetupHandler(new SSLSocketInitializer.SSLSocketInitializerWrapper(mSSLSocketInitializer))
                .setServerInfo("AndServer")
                .registerHandler("*", handler)
                .setExceptionLogger(ExceptionLogger.STD_ERR)
                .create();
        try {
            isRunning = true;
            mHttpServer.start();
            if (mListener != null) {
                Executors.getInstance().post(new Runnable() {
                    @Override
                    public void run() {
                        mListener.onStarted();
                    }
                });
            }
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    mHttpServer.shutdown(3, TimeUnit.SECONDS);
                }
            });
        } catch (final Exception e) {
            if (mListener != null) {
                Executors.getInstance().post(new Runnable() {
                    @Override
                    public void run() {
                        mListener.onError(e);
                    }
                });
            }
        }
    }

    /**
     * The current server is running.
     */
    final boolean isRunning() {
        return isRunning;
    }

    /**
     * The current server InetAddress.
     */
    final InetAddress getInetAddress() {
        if (isRunning())
            return mHttpServer.getInetAddress();
        return null;
    }

    /**
     * Stop core server.
     */
    void shutdown() {
        if (mHttpServer != null)
            mHttpServer.shutdown(3, TimeUnit.MINUTES);
        if (isInterrupted())
            interrupt();
        this.isRunning = false;

        if (mListener != null) {
            Executors.getInstance().post(new Runnable() {
                @Override
                public void run() {
                    mListener.onStopped();
                }
            });
        }
    }

    static final class Builder {

        private InetAddress mInetAddress;
        private int mPort;
        private int mTimeout;
        private SSLContext mSSLContext;
        private SSLSocketInitializer mSSLSocketInitializer;
        private Interceptor mInterceptor;
        private ExceptionResolver mExceptionResolver;
        private Map<String, RequestHandler> mRequestHandlerMap;
        private WebSite mWebSite;
        private StartupListener mListener;

        private Builder() {
        }

        Builder setInetAddress(InetAddress inetAddress) {
            this.mInetAddress = inetAddress;
            return this;
        }

        Builder setPort(int port) {
            this.mPort = port;
            return this;
        }

        Builder setTimeout(int timeout) {
            this.mTimeout = timeout;
            return this;
        }

        Builder setSSLContext(SSLContext sslContext) {
            this.mSSLContext = sslContext;
            return this;
        }

        Builder setSSLSocketInitializer(SSLSocketInitializer initializer) {
            this.mSSLSocketInitializer = initializer;
            return this;
        }

        Builder setInterceptor(Interceptor interceptor) {
            this.mInterceptor = interceptor;
            return this;
        }

        Builder setExceptionResolver(ExceptionResolver resolver) {
            this.mExceptionResolver = resolver;
            return this;
        }

        Builder setRequestHandlerMap(Map<String, RequestHandler> requestHandlerMap) {
            this.mRequestHandlerMap = requestHandlerMap;
            return this;
        }

        Builder setWebsite(WebSite webSite) {
            this.mWebSite = webSite;
            return this;
        }

        Builder setStartupListener(StartupListener listener) {
            this.mListener = listener;
            return this;
        }

        Core build() {
            return new Core(this);
        }
    }

    interface StartupListener {

        void onStarted();

        void onStopped();

        void onError(Exception e);
    }
}