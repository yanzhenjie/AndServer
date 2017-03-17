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
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;

/**
 * <p>Dealing with the client's request.</p>
 * Created by Yan Zhenjie on 2016/6/13.
 */
public interface RequestHandler {

    /**
     * When is the client request is triggered.
     *
     * @param request  {@link HttpRequest}.
     * @param response {@link HttpResponse}.
     * @param context  {@link HttpContext}.
     * @throws HttpException may be.
     * @throws IOException   read data.
     */
    void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException;
}