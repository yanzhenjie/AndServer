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
import androidx.annotation.Nullable;

import com.yanzhenjie.andserver.framework.view.ViewResolver;
import com.yanzhenjie.andserver.http.ResponseBody;
import com.yanzhenjie.andserver.util.MediaType;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;

/**
 * Created by Zhenjie Yan on 2018/9/6.
 */
public interface MessageConverter {

    /**
     * Convert a specific output to the response body. Some of the return values of handlers that cannot be recognized
     * by
     * {@link ViewResolver} require a message converter to be converted to a response body.
     *
     * @param output output of handle.
     * @param mediaType the content media type specified by the handler.
     */
    ResponseBody convert(@Nullable Object output, @Nullable MediaType mediaType);

    /**
     * Convert RequestBody to a object.
     *
     * @param stream {@link InputStream}.
     * @param mediaType he content media type.
     * @param type type of object.
     * @param <T> type of object.
     *
     * @return object.
     */
    @Nullable
    <T> T convert(@NonNull InputStream stream, @Nullable MediaType mediaType, Type type) throws IOException;
}