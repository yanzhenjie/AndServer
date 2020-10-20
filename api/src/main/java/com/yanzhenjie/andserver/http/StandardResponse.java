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
package com.yanzhenjie.andserver.http;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yanzhenjie.andserver.http.cookie.Cookie;
import com.yanzhenjie.andserver.http.cookie.CookieProcessor;
import com.yanzhenjie.andserver.http.cookie.StandardCookieProcessor;
import com.yanzhenjie.andserver.util.HttpDateFormat;
import com.yanzhenjie.andserver.util.MediaType;

import org.apache.httpcore.Header;
import org.apache.httpcore.HttpEntity;
import org.apache.httpcore.message.BasicHeader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Zhenjie Yan on 2018/6/12.
 */
public class StandardResponse implements HttpResponse {

    private static final CookieProcessor COOKIE_PROCESSOR = new StandardCookieProcessor();

    private org.apache.httpcore.HttpResponse mResponse;

    public StandardResponse(org.apache.httpcore.HttpResponse response) {
        this.mResponse = response;
    }

    @Override
    public void setStatus(int sc) {
        mResponse.setStatusCode(sc);
    }

    @Override
    public int getStatus() {
        return mResponse.getStatusLine().getStatusCode();
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
        Header header = mResponse.getFirstHeader(name);
        return header == null ? null : header.getValue();
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
    public List<String> getHeaders(@NonNull String name) {
        Header[] headers = mResponse.getHeaders(name);
        if (headers == null || headers.length == 0) {
            return Collections.emptyList();
        }

        List<String> list = new ArrayList<>();
        for (Header header: headers) {
            list.add(header.getValue());
        }
        return list;
    }

    @NonNull
    @Override
    public List<String> getHeaderNames() {
        Header[] headers = mResponse.getAllHeaders();
        if (headers == null || headers.length == 0) {
            return Collections.emptyList();
        }

        List<String> list = new ArrayList<>();
        for (Header header: headers) {
            list.add(header.getName());
        }
        return list;
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
    public void setBody(ResponseBody body) {
        mResponse.setEntity(new BodyToEntity(body));
    }

    private static class BodyToEntity implements HttpEntity {

        private ResponseBody mBody;

        private BodyToEntity(ResponseBody body) {
            this.mBody = body;
        }

        @Override
        public boolean isRepeatable() {
            return false;
        }

        @Override
        public boolean isChunked() {
            return false;
        }

        @Override
        public long getContentLength() {
            return mBody.contentLength();
        }

        @Override
        public Header getContentType() {
            MediaType mimeType = mBody.contentType();
            if (mimeType == null) {
                return null;
            }
            return new BasicHeader(CONTENT_TYPE, mimeType.toString());
        }

        @Override
        public Header getContentEncoding() {
            return null;
        }

        @Override
        public InputStream getContent() throws IOException {
            return null;
        }

        @Override
        public void writeTo(OutputStream out) throws IOException {
            mBody.writeTo(out);
        }

        @Override
        public boolean isStreaming() {
            return false;
        }

        @Override
        public void consumeContent() {
        }
    }
}