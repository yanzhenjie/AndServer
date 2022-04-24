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

import com.yanzhenjie.andserver.http.HttpRequest;
import com.yanzhenjie.andserver.http.HttpResponse;
import com.yanzhenjie.andserver.http.StandardRequest;
import com.yanzhenjie.andserver.http.StandardResponse;

public interface Http1StreamListener {

    void onRequestHead(HttpConnection connection, HttpRequest request);

    void onResponseHead(HttpConnection connection, HttpResponse response);

    void onExchangeComplete(HttpConnection connection, boolean keepAlive);

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    default org.apache.hc.core5.http.impl.Http1StreamListener wrapped() {
        return new org.apache.hc.core5.http.impl.Http1StreamListener() {
            @Override
            public void onRequestHead(org.apache.hc.core5.http.HttpConnection connection, org.apache.hc.core5.http.HttpRequest request) {
                Http1StreamListener.this.onRequestHead(new HttpConnectionWrapper(connection), new StandardRequest(request, null, null, null));
            }

            @Override
            public void onResponseHead(org.apache.hc.core5.http.HttpConnection connection, org.apache.hc.core5.http.HttpResponse response) {
                Http1StreamListener.this.onResponseHead(new HttpConnectionWrapper(connection), new StandardResponse(response));
            }

            @Override
            public void onExchangeComplete(org.apache.hc.core5.http.HttpConnection connection, boolean keepAlive) {
                Http1StreamListener.this.onExchangeComplete(new HttpConnectionWrapper(connection), keepAlive);
            }
        };
    }
}
