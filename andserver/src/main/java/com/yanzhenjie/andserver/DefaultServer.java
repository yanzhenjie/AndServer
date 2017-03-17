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

import com.yanzhenjie.andserver.website.WebSite;

import java.util.Map;

/**
 * <p>Server CoreThread. Mainly is to establish the service side, distribute the requests.</p>
 * Created by Yan Zhenjie on 2016/6/13.
 */
class DefaultServer implements Server {

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
     * Website.
     */
    private final WebSite mWebSite;

    /**
     * Server listener.
     */
    private Server.Listener mListener;

    /**
     * Core Thread.
     */
    private CoreThread mCore;

    DefaultServer(int port, int timeout, Map<String, RequestHandler> handlerMap, WebSite webSite, Server.Listener listener) {
        this.mPort = port;
        this.mTimeout = timeout;
        this.mHandlerMap = handlerMap;
        this.mWebSite = webSite;
        this.mListener = listener;
    }

    @Override
    public void start() {
        if (!isRunning()) {
            mCore = new CoreThread(mPort, mTimeout, mHandlerMap, mWebSite, mListener);
            mCore.start();
        }
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
}
