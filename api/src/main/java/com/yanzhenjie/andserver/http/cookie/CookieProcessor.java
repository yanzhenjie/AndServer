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
package com.yanzhenjie.andserver.http.cookie;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.apache.httpcore.Header;

import java.util.List;

/**
 * Created by Zhenjie Yan on 2018/7/27.
 */
public interface CookieProcessor {

    /**
     * Parse the provided headers into server cookie objects.
     *
     * @param headers the HTTP headers to parse.
     */
    @NonNull
    List<Cookie> parseCookieHeader(@Nullable Header[] headers);

    /**
     * Generate the {@code Set-Cookie} HTTP header value for the given {@code Cookie}.
     *
     * @param cookie the cookie for which the header will be generated.
     *
     * @return the header value in a form that can be added directly to the response.
     */
    @NonNull
    String generateHeader(@NonNull Cookie cookie);
}