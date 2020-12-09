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
package com.yanzhenjie.andserver.error;

import com.yanzhenjie.andserver.util.MediaType;
import com.yanzhenjie.andserver.http.StatusCode;

/**
 * Created by Zhenjie Yan on 2018/9/8.
 */
public class ContentNotSupportedException extends HttpException {

    private static final String MESSAGE = "The content type [%s] is not supported.";

    public ContentNotSupportedException(MediaType mediaType) {
        super(StatusCode.SC_UNSUPPORTED_MEDIA_TYPE, String.format(MESSAGE, mediaType));
    }

    public ContentNotSupportedException(MediaType mediaType, Throwable cause) {
        super(StatusCode.SC_UNSUPPORTED_MEDIA_TYPE, String.format(MESSAGE, mediaType), cause);
    }
}