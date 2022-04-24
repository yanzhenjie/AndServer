/*
 * Copyright (C) 2018 Zhenjie Yan
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
package com.yanzhenjie.andserver;

import com.yanzhenjie.andserver.delegate.CharCodingConfigDelegate;
import com.yanzhenjie.andserver.delegate.ConnectionReuseStrategy;
import com.yanzhenjie.andserver.delegate.Http1ConfigDelegate;
import com.yanzhenjie.andserver.delegate.Http1StreamListener;
import com.yanzhenjie.andserver.delegate.HttpProcessor;

import java.net.URISyntaxException;

/**
 * Created by Zhenjie Yan on 2018/9/10.
 */
public interface Server {

    /**
     * Server running status.
     *
     * @return return true, not return false.
     */
    boolean isRunning();

    /**
     * Start the server.
     */
    void startup();

    /**
     * Quit the server.
     */
    void shutdown();

    interface Builder<T extends Builder<T, S, L>, S extends Server, L extends ServerListener> {

        /**
         * Specify canonical hostname.
         */
        T setCanonicalHostName(String canonicalHostName);

        /**
         * Assigns {@link Http1ConfigDelegate} instance.
         */
        T setHttp1Config(Http1ConfigDelegate http1Config);

        /**
         * Assigns {@link CharCodingConfigDelegate} instance.
         */
        T setCharCodingConfig(CharCodingConfigDelegate charCodingConfig);

        /**
         * Assigns {@link HttpProcessor} instance.
         * Will be called before server class defined processor.
         */
        T setHttpProcessor(HttpProcessor httpProcessor);

        /**
         * Assigns {@link ConnectionReuseStrategy} instance.
         */
        T setConnectionReuseStrategy(ConnectionReuseStrategy connectionReuseStrategy);

        /**
         * Assigns {@link Http1StreamListener} instance.
         * Will be called after server class defined listener.
         */
        T setStreamListener(Http1StreamListener streamListener);

        /**
         * Set the server listener.
         */
        T setListener(L listener);

        S build();
    }

    interface ProxyBuilder<T extends ProxyBuilder<T, S, L>, S extends Server,
            L extends ServerListener> extends Builder<T, S, L> {

        /**
         * Add host address to proxy.
         *
         * @param hostName  such as: {@code www.example.com}, {@code api.example.com}, {@code 192.168.1.111}.
         * @param proxyHost such as: {@code http://127.0.0.1:8080}, {@code http://localhost:8181}
         */
        T addProxy(String hostName, String proxyHost) throws URISyntaxException;
    }

    interface ServerListener {

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