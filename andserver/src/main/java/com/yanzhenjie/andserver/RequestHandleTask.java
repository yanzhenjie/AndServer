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

import org.apache.http.HttpException;
import org.apache.http.HttpServerConnection;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpService;

import java.io.IOException;

/**
 * <p></p>
 * Created on 2016/6/13.
 *
 * @author Yan Zhenjie.
 */
public class RequestHandleTask extends Thread {

    private final HttpService mHttpService;

    private final HttpServerConnection mServerConnection;

    private DefaultAndServer mWebServerThread;

    public RequestHandleTask(DefaultAndServer webServerThread, HttpService httpservice, HttpServerConnection conn) {
        this.mWebServerThread = webServerThread;
        this.mHttpService = httpservice;
        this.mServerConnection = conn;
    }

    @Override
    public void run() {
        try {
            while (mWebServerThread.isLooping() && mServerConnection.isOpen()) {
                this.mHttpService.handleRequest(this.mServerConnection, new BasicHttpContext());
            }
        } catch (IOException e) {
        } catch (HttpException e) {
        } finally {
            try {
                this.mServerConnection.shutdown();
            } catch (IOException e) {
            }
        }
    }
}
