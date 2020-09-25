/*
 * Copyright © 2018 Zhenjie Yan.
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
package com.yanzhenjie.andserver.framework.handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yanzhenjie.andserver.framework.view.View;
import com.yanzhenjie.andserver.http.HttpRequest;
import com.yanzhenjie.andserver.http.HttpResponse;

import java.io.IOException;

/**
 * Created by Zhenjie Yan on 2018/8/28.
 */
public interface RequestHandler {

    /**
     * Can simply return {@code null} or empty if there's no support in this handler.
     *
     * @param request current request.
     *
     * @return the ETag value for this handler.
     */
    @Nullable
    String getETag(@NonNull HttpRequest request) throws Exception;

    /**
     * Can simply return -1 if there's no support in this handler.
     *
     * @param request current request.
     *
     * @return the {@code LastModified} value for resource.
     */
    long getLastModified(@NonNull HttpRequest request) throws Exception;

    /**
     * Use the given handler to handle this request.
     *
     * @param request current request.
     * @param response current response.
     *
     * @return the impression sent to the client.
     */
    View handle(@NonNull HttpRequest request, @NonNull HttpResponse response) throws Exception;
}