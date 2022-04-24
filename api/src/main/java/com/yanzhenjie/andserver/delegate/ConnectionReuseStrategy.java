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

import com.yanzhenjie.andserver.http.HttpContext;
import com.yanzhenjie.andserver.http.HttpRequest;
import com.yanzhenjie.andserver.http.HttpResponse;
import com.yanzhenjie.andserver.http.StandardContext;
import com.yanzhenjie.andserver.http.StandardRequest;
import com.yanzhenjie.andserver.http.StandardResponse;

public interface ConnectionReuseStrategy {
    /**
     * Decides whether a connection can be kept open after a request.
     * If this method returns {@code false}, the caller MUST
     * close the connection to correctly comply with the HTTP protocol.
     * If it returns {@code true}, the caller SHOULD attempt to
     * keep the connection open for reuse with another request.
     * <p>
     * One can use the HTTP context to retrieve additional objects that
     * may be relevant for the keep-alive strategy: the actual HTTP
     * connection, the original HTTP request, target host if known,
     * number of times the connection has been reused already and so on.
     * </p>
     * <p>
     * If the connection is already closed, {@code false} is returned.
     * The stale connection check MUST NOT be triggered by a
     * connection reuse strategy.
     * </p>
     *
     * @param request  The last request transmitted over that connection.
     * @param response The last response transmitted over that connection.
     * @param context  the context in which the connection is being
     *                 used.
     * @return {@code true} if the connection is allowed to be reused, or
     * {@code false} if it MUST NOT be reused
     */
    boolean keepAlive(HttpRequest request, HttpResponse response, HttpContext context);

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    default org.apache.hc.core5.http.ConnectionReuseStrategy wrapped() {
        return (request, response, context) -> {
            HttpContext contextWrapped = new StandardContext(context);
            return this.keepAlive(new StandardRequest(request, contextWrapped,
                            null, null),
                    new StandardResponse(response), contextWrapped);
        };
    }
}
