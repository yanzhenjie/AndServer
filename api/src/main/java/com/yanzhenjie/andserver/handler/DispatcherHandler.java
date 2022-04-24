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
package com.yanzhenjie.andserver.handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;

import com.yanzhenjie.andserver.framework.ExceptionResolver;
import com.yanzhenjie.andserver.framework.HandlerInterceptor;
import com.yanzhenjie.andserver.framework.MessageConverter;
import com.yanzhenjie.andserver.framework.config.Multipart;
import com.yanzhenjie.andserver.framework.handler.HandlerAdapter;
import com.yanzhenjie.andserver.http.HttpRequest;
import com.yanzhenjie.andserver.http.HttpResponse;
import com.yanzhenjie.andserver.http.RequestDispatcher;
import com.yanzhenjie.andserver.register.Register;

public interface DispatcherHandler extends Register {
    @Override
    void addAdapter(@NonNull HandlerAdapter adapter);

    @Override
    void addInterceptor(@NonNull HandlerInterceptor interceptor);

    @Override
    void setConverter(MessageConverter converter);

    @Override
    void setResolver(@NonNull ExceptionResolver resolver);

    @Override
    void setMultipart(Multipart multipart);

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    void handle(HttpRequest request, HttpResponse response);

    @Nullable
    RequestDispatcher getRequestDispatcher(HttpRequest request, String path);
}
