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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yanzhenjie.andserver.http.cookie.Cookie;

import java.util.List;

/**
 * Created by Zhenjie Yan on 2018/9/6.
 */
public class ResponseWrapper implements HttpResponse {

    private HttpResponse mResponse;

    public ResponseWrapper(HttpResponse response) {
        this.mResponse = response;
    }

    /**
     * Get the original response object.
     *
     * @return {@link HttpResponse}.
     */
    public HttpResponse getResponse() {
        return mResponse;
    }

    @Override
    public void setStatus(int sc) {
        mResponse.setStatus(sc);
    }

    @Override
    public int getStatus() {
        return mResponse.getStatus();
    }

    @Override
    public void setHeader(@NonNull String name, @NonNull String value) {
        mResponse.setHeader(name, value);
    }

    @Override
    public void addHeader(@NonNull String name, @NonNull String value) {
        mResponse.addHeader(name, value);
    }

    @Nullable
    @Override
    public String getHeader(@NonNull String name) {
        return mResponse.getHeader(name);
    }

    @Override
    public void setDateHeader(@NonNull String name, long date) {
        mResponse.setDateHeader(name, date);
    }

    @Override
    public void addDateHeader(@NonNull String name, long date) {
        mResponse.addDateHeader(name, date);
    }

    @Override
    public void setIntHeader(@NonNull String name, int value) {
        mResponse.setIntHeader(name, value);
    }

    @Override
    public void addIntHeader(@NonNull String name, int value) {
        mResponse.addIntHeader(name, value);
    }

    @Override
    public boolean containsHeader(@NonNull String name) {
        return mResponse.containsHeader(name);
    }

    @NonNull
    @Override
    public List<String> getHeaders(@NonNull String name) {
        return mResponse.getHeaders(name);
    }

    @NonNull
    @Override
    public List<String> getHeaderNames() {
        return mResponse.getHeaderNames();
    }

    @Override
    public void addCookie(@NonNull Cookie cookie) {
        mResponse.addCookie(cookie);
    }

    @Override
    public void sendRedirect(@NonNull String location) {
        mResponse.sendRedirect(location);
    }

    @Override
    public void setBody(ResponseBody body) {
        mResponse.setBody(body);
    }
}