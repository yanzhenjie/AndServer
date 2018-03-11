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
package com.yanzhenjie.andserver.protocol;

import org.apache.httpcore.HttpException;
import org.apache.httpcore.HttpRequest;

import java.io.IOException;

/**
 * Created by YanZhenjie on 2017/12/22.
 */
public interface LastModified {

    /**
     * The return value will be sent to the HTTP client as {@code Last-Modified }header,
     * and compared with {@code If-Modified-Since} headers that the client sends back.
     * The content will only get regenerated if there has been a modification.
     *
     * @param request current HTTP request.
     * @return the time the underlying resource was last modified,
     * {@code <0} meaning that the content must always be regenerated.
     */
    long getLastModified(HttpRequest request) throws HttpException, IOException;

}