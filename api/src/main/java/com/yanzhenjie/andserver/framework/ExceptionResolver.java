/*
 * Copyright 2018 Zhenjie Yan.
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

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.yanzhenjie.andserver.error.HttpException;
import com.yanzhenjie.andserver.error.MethodNotSupportException;
import com.yanzhenjie.andserver.framework.body.StringBody;
import com.yanzhenjie.andserver.http.HttpHeaders;
import com.yanzhenjie.andserver.http.HttpMethod;
import com.yanzhenjie.andserver.http.HttpRequest;
import com.yanzhenjie.andserver.http.HttpResponse;
import com.yanzhenjie.andserver.http.StatusCode;

import java.util.List;

/**
 * Created by Zhenjie Yan on 2018/8/8.
 */
public interface ExceptionResolver {

    class ResolverWrapper implements ExceptionResolver {

        private final ExceptionResolver mResolver;

        public ResolverWrapper(ExceptionResolver resolver) {
            this.mResolver = resolver;
        }

        @Override
        public void onResolve(@NonNull HttpRequest request, @NonNull HttpResponse response, @NonNull Throwable e) {
            if (e instanceof MethodNotSupportException) {
                List<HttpMethod> methods = ((MethodNotSupportException) e).getMethods();
                if (methods != null && methods.size() > 0) {
                    response.setHeader(HttpHeaders.ALLOW, TextUtils.join(", ", methods));
                }
            }
            mResolver.onResolve(request, response, e);
        }
    }

    ExceptionResolver DEFAULT = new ExceptionResolver() {
        @Override
        public void onResolve(@NonNull HttpRequest request, @NonNull HttpResponse response, @NonNull Throwable e) {
            if (e instanceof HttpException) {
                HttpException ex = (HttpException) e;
                response.setStatus(ex.getStatusCode());
            } else {
                response.setStatus(StatusCode.SC_INTERNAL_SERVER_ERROR);
            }
            response.setBody(new StringBody(e.getMessage()));
        }
    };

    /**
     * Resolve exceptions that occur in the program, replacing the default output information for the exception.
     *
     * @param request current request.
     * @param response current response.
     * @param e an exception occurred in the program.
     */
    void onResolve(@NonNull HttpRequest request, @NonNull HttpResponse response, @NonNull Throwable e);
}