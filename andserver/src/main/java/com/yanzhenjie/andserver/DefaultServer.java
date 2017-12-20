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
import com.yanzhenjie.andserver.website.WebSite;

import java.net.InetAddress;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;

/**
 * <p>
 * Server Core. Mainly is to establish the service side, distribute the requests.
 * </p>
 * Created by Yan Zhenjie on 2016/6/13.
 */
class DefaultServer implements Server {

    static Builder newBuilder() {
        return new Builder();
    }

    /**
     * Core Thread.
     */
    private Core mCore;

    private DefaultServer(Builder builder) {
        mCore = Core.newBuilder()
                .setInetAddress(builder.mInetAddress)
                .setPort(builder.mPort)
                .setTimeout(builder.mTimeout)
                .setSSLContext(builder.mSSLContext)
                .setSSLSocketInitializer(builder.mSSLSocketInitializer)
                .setInterceptor(builder.mInterceptor)
                .setExceptionResolver(builder.mExceptionResolver)
                .setRequestHandlerMap(builder.mRequestHandlerMap)
                .setWebsite(builder.mWebSite)
                .setStartupListener(builder.mListener)
                .build();
    }

    @Override
    public void start() {
        if (!isRunning()) {
            mCore.start();
        }
    }

    @Override
    public InetAddress getInetAddress() {
        if (isRunning())
            return mCore.getInetAddress();
        return null;
    }

    @Override
    public void stop() {
        if (isRunning())
            mCore.shutdown();
    }

    @Override
    public boolean isRunning() {
        return mCore != null && mCore.isRunning();
    }

    private static final class Builder implements Server.Builder {

        private InetAddress mInetAddress;
        private int mPort;
        private int mTimeout;
        private SSLContext mSSLContext;
        private SSLSocketInitializer mSSLSocketInitializer;
        private Interceptor mInterceptor;
        private ExceptionResolver mExceptionResolver;
        private Map<String, RequestHandler> mRequestHandlerMap;
        private WebSite mWebSite;
        private Server.Listener mListener;

        private Builder() {
            this.mRequestHandlerMap = new LinkedHashMap<>();
        }

        @Override
        public Server.Builder inetAddress(InetAddress inetAddress) {
            this.mInetAddress = inetAddress;
            return this;
        }

        @Override
        public Server.Builder port(int port) {
            this.mPort = port;
            return this;
        }

        @Override
        public Server.Builder timeout(int timeout, TimeUnit timeUnit) {
            long timeoutMs = timeUnit.toMillis(timeout);
            this.mTimeout = (int) Math.min(timeoutMs, Integer.MAX_VALUE);
            return this;
        }

        @Override
        public Server.Builder sslContext(SSLContext sslContext) {
            this.mSSLContext = sslContext;
            return this;
        }

        @Override
        public Server.Builder sslSocketInitializer(SSLSocketInitializer initializer) {
            this.mSSLSocketInitializer = initializer;
            return this;
        }

        @Override
        public Server.Builder interceptor(Interceptor interceptor) {
            this.mInterceptor = interceptor;
            return this;
        }

        @Override
        public Server.Builder exceptionResolver(ExceptionResolver resolver) {
            this.mExceptionResolver = resolver;
            return this;
        }

        @Override
        public Server.Builder registerHandler(String path, RequestHandler handler) {
            this.mRequestHandlerMap.put(path, handler);
            return this;
        }

        @Override
        public Server.Builder website(WebSite webSite) {
            this.mWebSite = webSite;
            return this;
        }

        @Override
        public Server.Builder listener(Listener listener) {
            this.mListener = listener;
            return this;
        }

        @Override
        public Server build() {
            return new DefaultServer(this);
        }
    }
}
