/*
 * Copyright 2018 Zhenjie Yan.
 *           2022 ISNing
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
package com.yanzhenjie.andserver.handler.classic;

import android.content.Context;

import com.yanzhenjie.andserver.AndServer;
import com.yanzhenjie.andserver.handler.BasicDispatcherHandler;
import com.yanzhenjie.andserver.http.HttpRequest;
import com.yanzhenjie.andserver.http.HttpResponse;
import com.yanzhenjie.andserver.http.StandardContext;
import com.yanzhenjie.andserver.http.StandardRequest;
import com.yanzhenjie.andserver.http.StandardResponse;
import com.yanzhenjie.andserver.http.session.SessionManager;
import com.yanzhenjie.andserver.http.session.StandardSessionManager;

import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.io.HttpRequestHandler;

/**
 * Created by Zhenjie Yan on 2018/8/8.
 */
public class ClassicDispatcherHandler extends BasicDispatcherHandler implements HttpRequestHandler {

    public static final String SUB_TAG = "ClassicDispatcherHandler";
    public static final String TAG = AndServer.genAndServerTag(SUB_TAG);

    private final SessionManager mSessionManager;

    public ClassicDispatcherHandler(Context context) {
        super(context);
        this.mSessionManager = new StandardSessionManager(context);
    }

    @Override
    public void handle(ClassicHttpRequest request,
                       ClassicHttpResponse response,
                       org.apache.hc.core5.http.protocol.HttpContext context) {
        HttpRequest requestWrapped = new StandardRequest(request, new StandardContext(context),
                this, mSessionManager);
        HttpResponse responseWrapped = new StandardResponse(response);
        handle(requestWrapped, responseWrapped);
    }
}