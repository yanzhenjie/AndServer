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
import androidx.annotation.Nullable;

/**
 * Created by Zhenjie Yan on 2018/8/31.
 */
public interface HttpContext {

    String RESPONSE_PRODUCE_TYPE = "http.response.Produce";

    String REQUEST_CREATED_SESSION = "http.request.Session";

    String HTTP_MESSAGE_CONVERTER = "http.message.converter";

    String ANDROID_CONTEXT = "android.context";

    /**
     * Obtains attribute with the given name.
     *
     * @param id the attribute name.
     *
     * @return attribute value, or {@code null} if not set.
     */
    @Nullable
    Object getAttribute(@NonNull String id);

    /**
     * Sets value of the attribute with the given name.
     *
     * @param id the attribute name.
     * @param obj the attribute value.
     */
    void setAttribute(@NonNull String id, @Nullable Object obj);

    /**
     * Removes attribute with the given name from the context.
     *
     * @param id the attribute name.
     *
     * @return attribute value, or {@code null} if not set.
     */
    @Nullable
    Object removeAttribute(@NonNull String id);
}