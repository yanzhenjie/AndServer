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
package com.yanzhenjie.andserver.framework.view;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yanzhenjie.andserver.error.NotFoundException;
import com.yanzhenjie.andserver.error.ServerInternalException;
import com.yanzhenjie.andserver.framework.MessageConverter;
import com.yanzhenjie.andserver.framework.body.StringBody;
import com.yanzhenjie.andserver.http.HttpContext;
import com.yanzhenjie.andserver.http.HttpHeaders;
import com.yanzhenjie.andserver.http.HttpRequest;
import com.yanzhenjie.andserver.http.HttpResponse;
import com.yanzhenjie.andserver.http.RequestDispatcher;
import com.yanzhenjie.andserver.http.ResponseBody;
import com.yanzhenjie.andserver.http.StatusCode;
import com.yanzhenjie.andserver.util.MediaType;
import com.yanzhenjie.andserver.util.Patterns;

/**
 * Created by Zhenjie Yan on 2018/8/31.
 */
public class ViewResolver implements Patterns, StatusCode, HttpHeaders {

    private MessageConverter mConverter;

    public ViewResolver() {
    }

    public ViewResolver(@Nullable MessageConverter converter) {
        this.mConverter = converter;
    }

    /**
     * Solve the view and convert the view to http package content.
     *
     * @param view current view.
     * @param request current request.
     * @param response current response.
     */
    public void resolve(@Nullable View view, @NonNull HttpRequest request, @NonNull HttpResponse response) {
        if (view == null) {
            return;
        }

        Object output = view.output();

        if (view.rest()) {
            resolveRest(output, request, response);
        } else {
            resolvePath(output, request, response);
        }
    }

    private void resolveRest(Object output, @NonNull HttpRequest request, @NonNull HttpResponse response) {
        if (output instanceof ResponseBody) {
            response.setBody((ResponseBody) output);
        } else if (mConverter != null) {
            response.setBody(mConverter.convert(output, obtainProduce(request)));
        } else if (output == null) {
            response.setBody(new StringBody(""));
        } else if (output instanceof String) {
            response.setBody(new StringBody(output.toString(), obtainProduce(request)));
        } else {
            response.setBody(new StringBody(output.toString()));
        }
    }

    @Nullable
    private MediaType obtainProduce(@NonNull HttpRequest request) {
        final Object mtAttribute = request.getAttribute(HttpContext.RESPONSE_PRODUCE_TYPE);
        if (mtAttribute instanceof MediaType) {
            return (MediaType) mtAttribute;
        }
        return null;
    }

    private void resolvePath(Object output, @NonNull HttpRequest request, @NonNull HttpResponse response) {
        if (output instanceof CharSequence) {
            final String action = output.toString();
            if (TextUtils.isEmpty(action)) {
                return;
            }

            // "redirect:(.)*"
            if (action.matches(REDIRECT)) {
                response.setStatus(SC_FOUND);
                if (action.length() >= 9) {
                    final String path = action.substring(9);
                    response.setHeader(LOCATION, path);
                }
            }
            // "forward:(.)*"
            else if (action.matches(FORWARD)) {
                final String path = action.substring(8);
                RequestDispatcher dispatcher = request.getRequestDispatcher(path);
                if (dispatcher != null) {
                    dispatcher.forward(request, response);
                } else {
                    throw new NotFoundException(path);
                }
            }
            // "/user/kevin"
            else if (action.matches(PATH)) {
                final String path = action + ".html";
                RequestDispatcher dispatcher = request.getRequestDispatcher(path);
                if (dispatcher != null) {
                    dispatcher.forward(request, response);
                } else {
                    throw new NotFoundException(path);
                }
            } else {
                throw new NotFoundException(action);
            }
        } else {
            throw new ServerInternalException(String.format("The return value of [%s] is not supported", output));
        }
    }
}