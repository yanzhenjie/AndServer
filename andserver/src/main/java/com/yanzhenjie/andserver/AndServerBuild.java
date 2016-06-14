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

import java.util.HashMap;
import java.util.Map;

/**
 * <p>AndServer entrance.</p>
 * Created on 2016/6/13.
 *
 * @author Yan Zhenjie.
 */
public class AndServerBuild {

    /**
     * Socket port.
     */
    private int port = 4477;
    /**
     * Timeout.
     */
    private int timeout = 8 * 1000;
    /**
     * Intercept list.
     */
    private Map<String, AndServerRequestHandler> mRequestHandlerMap;

    /**
     * Create {@link AndServerBuild}.
     *
     * @return {@link AndServerBuild}.
     */
    public static AndServerBuild create() {
        return new AndServerBuild();
    }

    private AndServerBuild() {
        mRequestHandlerMap = new HashMap<String, AndServerRequestHandler>();
    }

    /**
     * Add a intercept.
     *
     * @param intercept      intercept name.
     * @param requestHandler {@link AndServerRequestHandler}.
     */
    public void add(String intercept, AndServerRequestHandler requestHandler) {
        this.mRequestHandlerMap.put(intercept, requestHandler);
    }

    /**
     * Set socket sort.
     *
     * @param port port.
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * Set connection timeout.
     *
     * @param timeout ms.
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    /**
     * Build {@link AndServer}.
     *
     * @return {@link AndServer}.
     */
    public AndServer build() {
        return new DefaultAndServer(port, timeout, mRequestHandlerMap);
    }
}