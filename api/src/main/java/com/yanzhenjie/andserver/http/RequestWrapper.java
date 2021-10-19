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
import com.yanzhenjie.andserver.http.session.Session;
import com.yanzhenjie.andserver.util.MediaType;
import com.yanzhenjie.andserver.util.MultiValueMap;

import java.util.List;
import java.util.Locale;

/**
 * Created by Zhenjie Yan on 2018/9/6.
 */
public class RequestWrapper implements HttpRequest {

    private HttpRequest mRequest;

    public RequestWrapper(HttpRequest request) {
        this.mRequest = request;
    }

    /**
     * Get the original request object.
     *
     * @return {@link HttpRequest}.
     */
    public HttpRequest getRequest() {
        return mRequest;
    }

    @Override
    public String getLocalName() {
        return mRequest.getLocalName();
    }

    @Override
    public String getLocalAddr() {
        return mRequest.getLocalAddr();
    }

    @Override
    public int getLocalPort() {
        return mRequest.getLocalPort();
    }

    @Override
    public String getRemoteAddr() {
        return mRequest.getRemoteAddr();
    }

    @Override
    public String getRemoteHost() {
        return mRequest.getRemoteHost();
    }

    @Override
    public int getRemotePort() {
        return mRequest.getRemotePort();
    }

    @NonNull
    @Override
    public HttpMethod getMethod() {
        return mRequest.getMethod();
    }

    @NonNull
    @Override
    public String getURI() {
        return mRequest.getURI();
    }

    @NonNull
    @Override
    public String getPath() {
        return mRequest.getPath();
    }

    @NonNull
    @Override
    public List<String> getQueryNames() {
        return mRequest.getQueryNames();
    }

    @Nullable
    @Override
    public String getQuery(@NonNull String name) {
        return mRequest.getQuery(name);
    }

    @NonNull
    @Override
    public List<String> getQueries(@NonNull String name) {
        return mRequest.getQueries(name);
    }

    @NonNull
    @Override
    public MultiValueMap<String, String> getQuery() {
        return mRequest.getQuery();
    }

    @NonNull
    @Override
    public List<String> getHeaderNames() {
        return mRequest.getHeaderNames();
    }

    @Nullable
    @Override
    public String getHeader(@NonNull String name) {
        return mRequest.getHeader(name);
    }

    @NonNull
    @Override
    public List<String> getHeaders(@NonNull String name) {
        return mRequest.getHeaders(name);
    }

    @Override
    public long getDateHeader(@NonNull String name) {
        return mRequest.getDateHeader(name);
    }

    @Override
    public int getIntHeader(@NonNull String name) {
        return mRequest.getIntHeader(name);
    }

    @Nullable
    @Override
    public MediaType getAccept() {
        return mRequest.getAccept();
    }

    @NonNull
    @Override
    public List<MediaType> getAccepts() {
        return mRequest.getAccepts();
    }

    @NonNull
    @Override
    public Locale getAcceptLanguage() {
        return mRequest.getAcceptLanguage();
    }

    @NonNull
    @Override
    public List<Locale> getAcceptLanguages() {
        return mRequest.getAcceptLanguages();
    }

    @Nullable
    @Override
    public String getCookieValue(String name) {
        return mRequest.getCookieValue(name);
    }

    @Nullable
    @Override
    public Cookie getCookie(@NonNull String name) {
        return mRequest.getCookie(name);
    }

    @NonNull
    @Override
    public List<Cookie> getCookies() {
        return mRequest.getCookies();
    }

    @Override
    public long getContentLength() {
        return mRequest.getContentLength();
    }

    @Nullable
    @Override
    public MediaType getContentType() {
        return mRequest.getContentType();
    }

    @NonNull
    @Override
    public List<String> getParameterNames() {
        return mRequest.getParameterNames();
    }

    @Nullable
    @Override
    public String getParameter(@NonNull String name) {
        return mRequest.getParameter(name);
    }

    @NonNull
    @Override
    public List<String> getParameters(@NonNull String name) {
        return mRequest.getParameters(name);
    }

    @NonNull
    @Override
    public MultiValueMap<String, String> getParameter() {
        return mRequest.getParameter();
    }

    @Nullable
    @Override
    public RequestBody getBody() throws UnsupportedOperationException {
        return mRequest.getBody();
    }

    @NonNull
    @Override
    public Session getValidSession() {
        return mRequest.getValidSession();
    }

    @Nullable
    @Override
    public Session getSession() {
        return mRequest.getSession();
    }

    @Nullable
    @Override
    public String changeSessionId() {
        return mRequest.changeSessionId();
    }

    @Override
    public boolean isSessionValid() {
        return mRequest.isSessionValid();
    }

    @Nullable
    @Override
    public RequestDispatcher getRequestDispatcher(@NonNull String path) {
        return mRequest.getRequestDispatcher(path);
    }

    @Override
    public HttpContext getContext() {
        return mRequest.getContext();
    }

    @Nullable
    @Override
    public Object getAttribute(@NonNull String id) {
        return mRequest.getAttribute(id);
    }

    @Override
    public void setAttribute(@NonNull String id, @NonNull Object obj) {
        mRequest.setAttribute(id, obj);
    }

    @Nullable
    @Override
    public Object removeAttribute(@NonNull String id) {
        return mRequest.removeAttribute(id);
    }
}