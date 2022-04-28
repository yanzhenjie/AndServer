/*
 * Copyright (C) 2018 Zhenjie Yan
 *               2022 ISNing
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
import com.yanzhenjie.andserver.http.cookie.CookieProcessor;
import com.yanzhenjie.andserver.http.cookie.StandardCookieProcessor;
import com.yanzhenjie.andserver.util.HttpDateFormat;

import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.Header;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Zhenjie Yan on 2018/6/12.
 */
public class StandardResponse implements HttpResponse {

    private static final CookieProcessor COOKIE_PROCESSOR = new StandardCookieProcessor();

    private final org.apache.hc.core5.http.HttpResponse mResponse;
    private ResponseBody mBody;

    public StandardResponse(org.apache.hc.core5.http.HttpResponse response) {
        this.mResponse = response;
    }

    @Override
    public void setStatus(int sc) {
        mResponse.setCode(sc);
    }

    @Override
    public int getStatus() {
        return mResponse.getCode();
    }

    @Override
    public void setHeader(@NonNull String name, @NonNull String value) {
        mResponse.setHeader(name, value);
    }

    @Override
    public void addHeader(@NonNull String name, @NonNull String value) {
        mResponse.addHeader(name, value);
    }

    @Override
    public void setHeader(@NonNull com.yanzhenjie.andserver.http.Header header) {
        mResponse.setHeader(header.wrapped());
    }

    @Override
    public void addHeader(@NonNull com.yanzhenjie.andserver.http.Header header) {
        mResponse.addHeader(header.wrapped());
    }

    @Nullable
    @Override
    public com.yanzhenjie.andserver.http.Header getHeader(@NonNull String name) {
        Header header = mResponse.getFirstHeader(name);
        return header == null ? null : new HeaderWrapper(header);
    }

    @Override
    public void setDateHeader(@NonNull String name, long date) {
        setHeader(name, HttpDateFormat.formatDate(date));
    }

    @Override
    public void addDateHeader(@NonNull String name, long date) {
        addHeader(name, HttpDateFormat.formatDate(date));
    }

    @Override
    public void setIntHeader(@NonNull String name, int value) {
        setHeader(name, Integer.toString(value));
    }

    @Override
    public void addIntHeader(@NonNull String name, int value) {
        addHeader(name, Integer.toString(value));
    }

    @Override
    public boolean containsHeader(@NonNull String name) {
        return mResponse.containsHeader(name);
    }

    @NonNull
    @Override
    public List<com.yanzhenjie.andserver.http.Header> getHeaders(@NonNull String name) {
        Header[] headers = mResponse.getHeaders(name);
        if (headers == null || headers.length == 0) {
            return Collections.emptyList();
        }

        return HeaderWrapper.wrap(Arrays.asList(headers));
    }

    @NonNull
    @Override
    public List<com.yanzhenjie.andserver.http.Header> getHeaders() {
        Header[] headers = mResponse.getHeaders();
        if (headers == null || headers.length == 0) {
            return Collections.emptyList();
        }

        return HeaderWrapper.wrap(Arrays.asList(headers));
    }

    @Override
    public void addCookie(@NonNull Cookie cookie) {
        addHeader(SET_COOKIE, COOKIE_PROCESSOR.generateHeader(cookie));
    }

    @Override
    public void sendRedirect(@NonNull String location) {
        setStatus(SC_FOUND);
        setHeader(LOCATION, location);
    }

    @Override
    public ResponseBody getBody() {
        return mBody;
    }

    @Override
    public void setBody(ResponseBody body) {
        if (mResponse instanceof ClassicHttpResponse)
            ((ClassicHttpResponse) mResponse).setEntity(body.toEntity());
        mBody = body;
    }
}