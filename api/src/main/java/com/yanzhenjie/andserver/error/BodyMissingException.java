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

import com.yanzhenjie.andserver.http.StatusCode;

/**
 * Created by Zhenjie Yan on 2018/9/10.
 */
public class BodyMissingException extends HttpException {

    private static final String MESSAGE = "RequestBody is missing.";

    public BodyMissingException() {
        super(StatusCode.SC_BAD_REQUEST, MESSAGE);
    }

    public BodyMissingException(Throwable cause) {
        super(StatusCode.SC_BAD_REQUEST, MESSAGE, cause);
    }

}