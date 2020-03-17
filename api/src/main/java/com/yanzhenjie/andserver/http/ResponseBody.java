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
import java.io.OutputStream;

/**
 * Created by Zhenjie Yan on 2018/8/3.
 */
public interface ResponseBody {

    /**
     * Can it be reused?
     *
     * @return true, otherwise is false.
     */
    boolean isRepeatable();

    /**
     * Get the content-length of the message body, if the length is unknown, return a negative value.
     *
     * @return message length.
     */
    long contentLength();

    /**
     * Get the content-type of the message body, including charset.
     *
     * @return e.g. {@code application/json; charset=utf-8}.
     */
    @Nullable
    MediaType contentType();

    /**
     * Write the body to the output stream.
     *
     * @param output the output stream to write the body.
     */
    void writeTo(@NonNull OutputStream output) throws IOException;

}