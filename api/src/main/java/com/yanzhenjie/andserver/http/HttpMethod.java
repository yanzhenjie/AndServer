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

import java.util.Locale;

/**
 * Created by Zhenjie Yan on 2018/8/29.
 */
public enum HttpMethod {

    GET("GET"),
    HEAD("HEAD"),
    POST("POST"),
    PUT("PUT"),
    PATCH("PATCH"),
    DELETE("DELETE"),
    OPTIONS("OPTIONS"),
    TRACE("TRACE");

    private String value;

    HttpMethod(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    /**
     * Whether to allow the body to be transmitted.
     *
     * @return true, otherwise is false.
     */
    public boolean allowBody() {
        switch (this) {
            case POST:
            case PUT:
            case PATCH:
            case DELETE:
                return true;
            default:
                return false;
        }
    }

    /**
     * Reverse the text for the request value.
     *
     * @param method value text, such as: GET, POST.
     *
     * @return {@link HttpMethod}.
     */
    public static HttpMethod reverse(String method) {
        method = method.toUpperCase(Locale.ENGLISH);
        switch (method) {
            case "GET": {
                return GET;
            }
            case "HEAD": {
                return HEAD;
            }
            case "POST": {
                return POST;
            }
            case "PUT": {
                return PUT;
            }
            case "PATCH": {
                return PATCH;
            }
            case "DELETE": {
                return DELETE;
            }
            case "OPTIONS": {
                return OPTIONS;
            }
            case "TRACE": {
                return TRACE;
            }
            default: {
                String message = String.format("The value %1$s is not supported.", method);
                throw new UnsupportedOperationException(message);
            }
        }
    }

    @Override
    public String toString() {
        return value;
    }
}