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
package com.yanzhenjie.andserver.http;

import androidx.annotation.NonNull;

/**
 * Created by Zhenjie Yan on 2018/8/31.
 */
public interface RequestDispatcher {

    /**
     * Forwards a request from a handler to another handler on the server.
     *
     * @param request the current request.
     * @param response the current response.
     */
    void forward(@NonNull HttpRequest request, @NonNull HttpResponse response);
}