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

import com.yanzhenjie.andserver.website.AssetsWebsite;
import com.yanzhenjie.andserver.website.StorageWebsite;
import com.yanzhenjie.andserver.website.WebSite;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>Server entrance.</p>
 * Created by Yan Zhenjie on 2016/6/13.
 */
public class AndServer {

    /**
     * Socket mPort.
     */
    private final int mPort;

    /**
     * Timeout.
     */
    private final int mTimeout;

    /**
     * Intercept list.
     */
    private final Map<String, RequestHandler> mHandlerMap;

    /**
     * WebSite.
     */
    private final WebSite mWebSite;

    /**
     * Server listener.
     */
    private final Server.Listener mListener;

    private AndServer(Build build) {
        this.mPort = build.mPort;
        this.mTimeout = build.mTimeout;
        this.mHandlerMap = build.mHandlerMap;
        this.mWebSite = build.mWebSite;
        this.mListener = build.mListener;
    }

    /**
     * Create server.
     *
     * @return {@link Server}.
     */
    public Server createServer() {
        return new DefaultServer(mPort, mTimeout, mHandlerMap, mWebSite, mListener);
    }

    public static final class Build {

        /**
         * Port.
         */
        private int mPort = 8080;

        /**
         * Timeout.
         */
        private int mTimeout = 10 * 1000;

        /**
         * Intercept list.
         */
        private Map<String, RequestHandler> mHandlerMap;
        /**
         * WebSite.
         */
        private WebSite mWebSite;

        /**
         * Server listener.
         */
        private Server.Listener mListener;

        /**
         * Create Build.
         */
        public Build() {
            mHandlerMap = new HashMap<>(2);
        }

        /**
         * Set mPort.
         *
         * @param mPort number.
         */
        public Build port(int mPort) {
            this.mPort = mPort;
            return this;
        }

        /**
         * Set response mTimeout.
         *
         * @param mTimeout millisecond.
         */
        public Build timeout(int mTimeout) {
            this.mTimeout = mTimeout;
            return this;
        }

        /**
         * Register request handler.
         *
         * @param name    handler name.
         * @param handler {@link RequestHandler}.
         */
        public Build registerHandler(String name, RequestHandler handler) {
            mHandlerMap.put(name, handler);
            return this;
        }

        /**
         * Set website.
         *
         * @param mWebSite default {@link AssetsWebsite}, {@link StorageWebsite}.
         */
        public Build website(WebSite mWebSite) {
            this.mWebSite = mWebSite;
            return this;
        }

        /**
         * Set server listener.
         *
         * @param listener {@link Server.Listener}.
         * @return
         */
        public Build listener(Server.Listener listener) {
            this.mListener = listener;
            return this;
        }

        /**
         * Create AndServer.
         *
         * @return {@link AndServer}.
         */
        public AndServer build() {
            return new AndServer(this);
        }
    }
}