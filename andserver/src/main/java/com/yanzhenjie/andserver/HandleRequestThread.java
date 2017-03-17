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

import org.apache.http.HttpServerConnection;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpService;

import java.io.IOException;

/**
 * Created by Yan Zhenjie on 2016/6/13.
 */
public class HandleRequestThread extends Thread {

    private final HttpService mHttpService;

    private final HttpServerConnection mConnection;

    private CoreThread mCore;

    public HandleRequestThread(CoreThread core, HttpService httpservice, HttpServerConnection connection) {
        this.mCore = core;
        this.mHttpService = httpservice;
        this.mConnection = connection;
    }

    @Override
    public void run() {
        try {
            while (mCore.isRunning() && mConnection.isOpen()) {
                mHttpService.handleRequest(mConnection, new BasicHttpContext());
            }
        } catch (Exception ignored) {
        } finally {
            try {
                mConnection.shutdown();
            } catch (IOException ignored) {
            }
        }
    }
}
