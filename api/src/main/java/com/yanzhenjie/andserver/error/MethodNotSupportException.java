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

import com.yanzhenjie.andserver.http.HttpMethod;
import com.yanzhenjie.andserver.http.StatusCode;

import java.util.List;

/**
 * Created by Zhenjie Yan on 2018/7/19.
 */
public class MethodNotSupportException extends HttpException {

    private static final String MESSAGE = "The request method [%s] is not supported.";

    private List<HttpMethod> mMethods;

    public MethodNotSupportException(HttpMethod method) {
        super(StatusCode.SC_METHOD_NOT_ALLOWED, String.format(MESSAGE, method.value()));
    }

    public MethodNotSupportException(HttpMethod method, Throwable cause) {
        super(StatusCode.SC_METHOD_NOT_ALLOWED, String.format(MESSAGE, method.value()), cause);
    }

    public List<HttpMethod> getMethods() {
        return mMethods;
    }

    public void setMethods(List<HttpMethod> methods) {
        mMethods = methods;
    }
}