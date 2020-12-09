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
package com.yanzhenjie.andserver.error;

import com.yanzhenjie.andserver.http.StatusCode;

/**
 * Created by Zhenjie Yan on 2018/9/4.
 */
public class ServerInternalException extends HttpException {

    private static final String MESSAGE = "Server internal error";

    public ServerInternalException(String subMessage) {
        super(StatusCode.SC_INTERNAL_SERVER_ERROR, String.format("%s, %s.", MESSAGE, subMessage));
    }

    public ServerInternalException(String subMessage, Throwable cause) {
        super(StatusCode.SC_INTERNAL_SERVER_ERROR, String.format("%s, %s.", MESSAGE, subMessage), cause);
    }

    public ServerInternalException(Throwable cause) {
        super(StatusCode.SC_INTERNAL_SERVER_ERROR, String.format("%s.", MESSAGE), cause);
    }
}