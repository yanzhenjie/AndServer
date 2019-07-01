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
package com.yanzhenjie.andserver.http;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yanzhenjie.andserver.util.MediaType;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Zhenjie Yan on 2018/8/9.
 */
public interface RequestBody {

    /**
     * Retrieve the character encoding for the request.
     *
     * @return the character encoding for the request.
     */
    String contentEncoding();

    /**
     * Get the {@code Content-Length} of the message body, if the length is unknown, return a negative value.
     *
     * @return message length.
     */
    long length();

    /**
     * Get the {@code Content-Type} of the message body, including charset.
     *
     * @return e.g. {@code application/json; charset=utf-8}, or {@code null} if the content type is unknown.
     */
    @Nullable
    MediaType contentType();

    /**
     * Returns a content stream of this body.
     *
     * @return content stream of this body.
     *
     * @throws IOException if the stream could not be created.
     */
    @NonNull
    InputStream stream() throws IOException;

    /**
     * Convert the request body to a String.
     *
     * @return string.
     *
     * @throws IOException if the stream could not be created.
     */
    @NonNull
    String string() throws IOException;
}