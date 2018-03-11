/*
 * Copyright © 2017 Yan Zhenjie.
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
package com.yanzhenjie.andserver.exception;

import org.apache.httpcore.HttpEntity;
import org.apache.httpcore.HttpException;
import org.apache.httpcore.entity.ContentType;
import org.apache.httpcore.entity.StringEntity;

/**
 * Created by YanZhenjie on 2017/12/19.
 */
public class BaseException extends HttpException {

    private int mHttpCode;
    private HttpEntity mHttpBody;

    public BaseException() {
        this(500, "Unknown exception occurred on server.");
    }

    public BaseException(int httpCode, String httpBody) {
        super(httpBody);
        this.mHttpCode = httpCode;
        this.mHttpBody = new StringEntity(httpBody, ContentType.TEXT_PLAIN);
    }

    public int getHttpCode() {
        return mHttpCode;
    }

    public HttpEntity getHttpBody() {
        return mHttpBody;
    }
}