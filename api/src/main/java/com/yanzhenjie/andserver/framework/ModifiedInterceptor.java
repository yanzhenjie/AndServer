/*
 * Copyright 2018 Yan Zhenjie.
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
package com.yanzhenjie.andserver.framework;

import android.support.annotation.NonNull;
import android.util.Log;

import com.yanzhenjie.andserver.AndServer;
import com.yanzhenjie.andserver.framework.handler.RequestHandler;
import com.yanzhenjie.andserver.http.HttpMethod;
import com.yanzhenjie.andserver.http.HttpRequest;
import com.yanzhenjie.andserver.http.HttpResponse;
import com.yanzhenjie.andserver.http.Modified;

import java.io.IOException;

/**
 * Created by YanZhenjie on 2018/9/14.
 */
public class ModifiedInterceptor implements HandlerInterceptor {

    @Override
    public boolean onIntercept(@NonNull HttpRequest request, @NonNull HttpResponse response,
        @NonNull RequestHandler handler) throws Exception {
        // Process cache header, if supported by the handler.
        HttpMethod method = request.getMethod();
        if (method == HttpMethod.GET || method == HttpMethod.HEAD) {
            String eTag = null;
            try {
                eTag = handler.getETag(request);
            } catch (IOException ignored) {
                Log.w(AndServer.TAG, ignored.getMessage());
            }
            long lastModified = -1;
            try {
                lastModified = handler.getLastModified(request);
            } catch (IOException ignored) {
                Log.w(AndServer.TAG, ignored.getMessage());
            }
            return new Modified(request, response).process(eTag, lastModified);
        }
        return false;
    }
}