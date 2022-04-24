/*
 * Copyright (C) 2022 ISNing
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
package com.yanzhenjie.andserver.delegate;

import androidx.annotation.RestrictTo;

import com.yanzhenjie.andserver.error.HttpException;
import com.yanzhenjie.andserver.http.HttpContext;
import com.yanzhenjie.andserver.http.HttpRequest;
import com.yanzhenjie.andserver.http.HttpResponse;
import com.yanzhenjie.andserver.http.StandardContext;
import com.yanzhenjie.andserver.http.StandardRequest;
import com.yanzhenjie.andserver.http.StandardResponse;

import java.io.IOException;

public interface HttpProcessor {
    /**
     * Processes a request.
     * On the client side, this step is performed before the request is
     * sent to the server. On the server side, this step is performed
     * on incoming messages before the message body is evaluated.
     *
     * @param request the request to process
     * @param entity  the request entity or {@code null} if not available
     * @param context the context for the request
     * @throws HttpException in case of an HTTP protocol violation
     * @throws IOException   in case of an I/O error
     */
    void process(HttpRequest request, EntityDetails entity, HttpContext context) throws HttpException, IOException;

    /**
     * Processes a response.
     * On the server side, this step is performed before the response is
     * sent to the client. On the client side, this step is performed
     * on incoming messages before the message body is evaluated.
     *
     * @param response the response to process
     * @param entity   the request entity or {@code null} if not available
     * @param context  the context for the request
     * @throws HttpException in case of an HTTP protocol violation (Status code will be ignored)
     * @throws IOException   in case of an I/O error
     * @see org.apache.hc.core5.http.protocol.HttpProcessor
     */
    void process(HttpResponse response, EntityDetails entity, HttpContext context) throws HttpException, IOException;

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    default org.apache.hc.core5.http.protocol.HttpProcessor wrapped() {
        return new org.apache.hc.core5.http.protocol.HttpProcessor() {
            @Override
            public void process(org.apache.hc.core5.http.HttpRequest request,
                                org.apache.hc.core5.http.EntityDetails entity,
                                org.apache.hc.core5.http.protocol.HttpContext context)
                    throws org.apache.hc.core5.http.HttpException, IOException {
                try {
                    HttpContext contextWrapped = new StandardContext(context);
                    StandardRequest requestWrapped = new StandardRequest(request, contextWrapped,
                            null, null);
                    HttpProcessor.this.process(requestWrapped, new EntityDetailsWrapper(entity), contextWrapped);
                } catch (HttpException e) {
                    String message = e.getMessage();
                    throw new org.apache.hc.core5.http.HttpException(message == null ?
                            "Internal Server Error" : message, e);
                }
            }

            @Override
            public void process(org.apache.hc.core5.http.HttpResponse response,
                                org.apache.hc.core5.http.EntityDetails entity,
                                org.apache.hc.core5.http.protocol.HttpContext context)
                    throws org.apache.hc.core5.http.HttpException, IOException {
                try {
                    HttpContext contextWrapped = new StandardContext(context);
                    StandardResponse requestWrapped = new StandardResponse(response);
                    HttpProcessor.this.process(requestWrapped, new EntityDetailsWrapper(entity), contextWrapped);
                } catch (HttpException e) {
                    String message = e.getMessage();
                    throw new org.apache.hc.core5.http.HttpException(message == null ?
                            "Internal Server Error" : message, e);
                }
            }
        };
    }
}
