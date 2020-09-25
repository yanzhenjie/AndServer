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
package com.yanzhenjie.andserver.framework;

import androidx.annotation.NonNull;

import com.yanzhenjie.andserver.http.HttpRequest;

/**
 * Created by Zhenjie Yan on 2018/8/29.
 */
public interface LastModified {

    /**
     * The return value will be sent to the HTTP client as {@code Last-Modified} header, and compared with {@code
     * If-Modified-Since} headers that the client sends back. The content will only get regenerated if there has been a
     * modification.
     *
     * @param request current request
     *
     * @return the time the underlying resource was last modified, or -1 meaning that the content must always be
     *     regenerated.
     */
    long getLastModified(@NonNull HttpRequest request) throws Throwable;
}