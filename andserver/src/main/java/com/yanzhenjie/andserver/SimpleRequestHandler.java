/*
 * Copyright © 2017 Yan Zhenjie.
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

import com.yanzhenjie.andserver.view.View;

import org.apache.httpcore.HttpException;
import org.apache.httpcore.HttpRequest;
import org.apache.httpcore.HttpResponse;
import org.apache.httpcore.protocol.HttpContext;

import java.io.IOException;

/**
 * Created by YanZhenjie on 2017/12/20.
 */
public class SimpleRequestHandler implements RequestHandler {

    @Override
    public final void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
        View view = handle(request, response);
        response.setStatusCode(view.getHttpCode());
        response.setEntity(view.getHttpEntity());
        response.setHeaders(view.getHeaders());
    }

    protected View handle(HttpRequest request, HttpResponse response) throws HttpException, IOException {
        return handle(request);
    }

    protected View handle(HttpRequest request) throws HttpException, IOException {
        return new View(200);
    }
}