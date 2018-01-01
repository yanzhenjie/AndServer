/*
 * Copyright © Yan Zhenjie. All Rights Reserved
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
package com.yanzhenjie.andserver.exception.resolver;

import com.yanzhenjie.andserver.view.ResponseStub;
import com.yanzhenjie.andserver.exception.BaseException;

import org.apache.httpcore.HttpEntity;
import org.apache.httpcore.HttpRequest;
import org.apache.httpcore.HttpResponse;
import org.apache.httpcore.entity.ContentType;
import org.apache.httpcore.entity.StringEntity;
import org.apache.httpcore.protocol.HttpContext;

/**
 * Created by YanZhenjie on 2017/12/20.
 */
public class SimpleExceptionResolver implements ExceptionResolver {

    @Override
    public final void resolveException(Exception e, HttpRequest request, HttpResponse response, HttpContext context) {
        ResponseStub responseStub = resolveException(e, request, response);
        response.setStatusCode(responseStub.getHttpCode());
        response.setEntity(responseStub.getHttpEntity());
        response.setHeaders(responseStub.getHeaders());
    }

    public ResponseStub resolveException(Exception e, HttpRequest request, HttpResponse response) {
        return resolveException(e);
    }

    protected ResponseStub resolveException(Exception e) {
        if (e instanceof BaseException) {
            BaseException exception = (BaseException) e;
            return new ResponseStub(exception.getHttpCode(), exception.getHttpBody());
        }
        String message = String.format("Server error occurred:\n%1$s", e.getMessage());
        HttpEntity httpEntity = new StringEntity(message, ContentType.TEXT_PLAIN);
        return new ResponseStub(500, httpEntity);
    }
}