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

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yanzhenjie.andserver.DispatcherHandler;
import com.yanzhenjie.andserver.http.cookie.Cookie;
import com.yanzhenjie.andserver.http.cookie.CookieProcessor;
import com.yanzhenjie.andserver.http.cookie.StandardCookieProcessor;
import com.yanzhenjie.andserver.http.session.Session;
import com.yanzhenjie.andserver.http.session.SessionManager;
import com.yanzhenjie.andserver.util.HttpDateFormat;
import com.yanzhenjie.andserver.util.IOUtils;
import com.yanzhenjie.andserver.util.LinkedMultiValueMap;
import com.yanzhenjie.andserver.util.MediaType;
import com.yanzhenjie.andserver.util.MimeType;
import com.yanzhenjie.andserver.util.MultiValueMap;
import com.yanzhenjie.andserver.util.UrlCoder;

import org.apache.commons.io.Charsets;
import org.apache.httpcore.Header;
import org.apache.httpcore.HttpEntity;
import org.apache.httpcore.HttpEntityEnclosingRequest;
import org.apache.httpcore.RequestLine;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.zip.GZIPInputStream;

/**
 * Created by Zhenjie Yan on 2018/6/12.
 */
public class StandardRequest implements HttpRequest {

    private static final CookieProcessor COOKIE_PROCESSOR = new StandardCookieProcessor();

    private org.apache.httpcore.HttpRequest mRequest;
    private HttpContext mContext;
    private DispatcherHandler mHandler;
    private RequestLine mRequestLine;
    private SessionManager mSessionManager;

    private Uri mUri;
    private boolean isParsedUri;

    private MultiValueMap<String, String> mQuery;
    private boolean isParsedQuery;

    private List<MediaType> mAccepts;
    private boolean isParsedAccept;

    private List<Locale> mLocales;
    private boolean isParsedLocale;

    private MultiValueMap<String, String> mParameter;
    private boolean isParsedParameter;

    public StandardRequest(org.apache.httpcore.HttpRequest request, HttpContext context, DispatcherHandler handler,
                           SessionManager sessionManager) {
        this.mRequest = request;
        this.mContext = context;
        this.mHandler = handler;
        this.mRequestLine = request.getRequestLine();
        this.mSessionManager = sessionManager;
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
        return HttpMethod.reverse(mRequestLine.getMethod());
    }

    @NonNull
    @Override
    public String getURI() {
        parseUri();
        return mUri.toString();
    }

    private void parseUri() {
        if (isParsedUri) {
            return;
        }

        String requestLine = mRequestLine.getUri();
        if (TextUtils.isEmpty(requestLine)) {
            requestLine = "/";
        }

        String uri = "scheme://host:ip" + requestLine;
        mUri = Uri.newBuilder(uri).build();
        isParsedUri = true;
    }

    public void setPath(String path) {
        parseUri();
        mUri = mUri.builder().setPath(path).build();
    }

    @NonNull
    @Override
    public String getPath() {
        parseUri();
        return mUri.getPath();
    }

    @NonNull
    @Override
    public List<String> getQueryNames() {
        parseQuery();
        return new LinkedList<>(mQuery.keySet());
    }

    @Nullable
    @Override
    public String getQuery(@NonNull String name) {
        parseQuery();
        return mQuery.getFirst(name);
    }

    @NonNull
    @Override
    public List<String> getQueries(@NonNull String name) {
        parseQuery();
        List<String> values = mQuery.get(name);
        return (values == null || values.isEmpty()) ? Collections.emptyList() : values;
    }

    @NonNull
    @Override
    public MultiValueMap<String, String> getQuery() {
        parseQuery();
        return mQuery;
    }

    private void parseQuery() {
        if (isParsedQuery) {
            return;
        }
        parseUri();

        mQuery = mUri.getParams();
        isParsedQuery = true;
    }

    @NonNull
    @Override
    public List<String> getHeaderNames() {
        Header[] headers = mRequest.getAllHeaders();
        if (headers == null || headers.length == 0) {
            return Collections.emptyList();
        }

        List<String> nameList = new ArrayList<>();
        for (Header header: headers) {
            nameList.add(header.getName());
        }
        return nameList;
    }

    @Nullable
    @Override
    public String getHeader(@NonNull String name) {
        Header header = mRequest.getFirstHeader(name);
        return header == null ? null : header.getValue();
    }

    @NonNull
    @Override
    public List<String> getHeaders(@NonNull String name) {
        Header[] headers = mRequest.getHeaders(name);
        if (headers == null || headers.length == 0) {
            return Collections.emptyList();
        }

        List<String> valueList = new ArrayList<>();
        for (Header header: headers) {
            valueList.add(header.getValue());
        }
        return valueList;
    }

    @Override
    public long getDateHeader(@NonNull String name) {
        Header header = mRequest.getFirstHeader(name);
        if (header == null) {
            return -1;
        }

        String value = header.getValue();
        long date = HttpDateFormat.parseDate(value);

        if (date == -1) {
            String message = String.format("The %s cannot be converted to date.", value);
            throw new IllegalStateException(message);
        }

        return date;
    }

    @Override
    public int getIntHeader(@NonNull String name) {
        Header header = mRequest.getFirstHeader(name);
        if (header == null) {
            return -1;
        }

        try {
            return Integer.parseInt(header.getValue());
        } catch (NumberFormatException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    @Nullable
    @Override
    public MediaType getAccept() {
        List<MediaType> mediaTypes = getAccepts();
        return mediaTypes.isEmpty() ? null : mediaTypes.get(0);
    }

    @NonNull
    @Override
    public List<MediaType> getAccepts() {
        parseAccept();
        return mAccepts;
    }

    private void parseAccept() {
        if (isParsedAccept) {
            return;
        }

        mAccepts = new ArrayList<>();
        Header[] headers = mRequest.getHeaders(ACCEPT);
        if (headers != null && headers.length > 0) {
            for (Header header: headers) {
                List<MediaType> mediaTypes = MediaType.parseMediaTypes(header.getValue());
                mAccepts.addAll(mediaTypes);
            }
        }
        if (mAccepts.isEmpty()) {
            mAccepts.add(MediaType.ALL);
        }

        isParsedAccept = true;
    }

    @NonNull
    @Override
    public Locale getAcceptLanguage() {
        return getAcceptLanguages().get(0);
    }

    @NonNull
    @Override
    public List<Locale> getAcceptLanguages() {
        parseLocale();
        return mLocales;
    }

    private void parseLocale() {
        if (isParsedLocale) {
            return;
        }

        mLocales = new ArrayList<>();
        Header[] headers = mRequest.getHeaders(ACCEPT_LANGUAGE);
        if (headers != null && headers.length > 0) {
            for (Header header: headers) {
                List<AcceptLanguage> acceptLanguages = AcceptLanguage.parse(header.getValue());
                for (AcceptLanguage acceptLanguage: acceptLanguages) {
                    mLocales.add(acceptLanguage.getLocale());
                }
            }
        }
        if (mLocales.isEmpty()) {
            mLocales.add(Locale.getDefault());
        }

        isParsedLocale = true;
    }

    @Nullable
    @Override
    public String getCookieValue(String name) {
        Cookie cookie = getCookie(name);
        if (cookie != null) {
            return cookie.getValue();
        }
        return null;
    }

    @Nullable
    @Override
    public Cookie getCookie(@NonNull String name) {
        List<Cookie> cookies = getCookies();
        if (cookies.isEmpty()) {
            return null;
        }

        for (Cookie cookie: cookies) {
            if (name.equalsIgnoreCase(cookie.getName())) {
                return cookie;
            }
        }
        return null;
    }

    @NonNull
    @Override
    public List<Cookie> getCookies() {
        return COOKIE_PROCESSOR.parseCookieHeader(mRequest.getHeaders(COOKIE));
    }

    @Override
    public long getContentLength() {
        String contentLength = getHeader(CONTENT_LENGTH);
        if (TextUtils.isEmpty(contentLength)) {
            return -1;
        }
        try {
            return Long.parseLong(contentLength);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    @Nullable
    @Override
    public MediaType getContentType() {
        String contentType = getHeader(CONTENT_TYPE);
        if (TextUtils.isEmpty(contentType)) {
            return null;
        }
        return MediaType.valueOf(contentType);
    }

    @NonNull
    @Override
    public List<String> getParameterNames() {
        parseParameter();
        List<String> paramNames = new LinkedList<>(mParameter.keySet());
        List<String> names = getQueryNames();
        if (!names.isEmpty()) {
            paramNames.addAll(names);
        }
        return paramNames;
    }

    @Nullable
    @Override
    public String getParameter(@NonNull String name) {
        parseParameter();
        String value = mParameter.getFirst(name);
        return TextUtils.isEmpty(value) ? getQuery(name) : value;
    }

    @NonNull
    @Override
    public List<String> getParameters(@NonNull String name) {
        parseParameter();
        List<String> values = mParameter.get(name);
        if (values == null || values.isEmpty()) {
            return getQueries(name);
        }
        return values;
    }

    @NonNull
    @Override
    public MultiValueMap<String, String> getParameter() {
        parseParameter();
        return mParameter.isEmpty() ? getQuery() : mParameter;
    }

    private void parseParameter() {
        if (isParsedParameter) {
            return;
        }

        if (!getMethod().allowBody()) {
            mParameter = new LinkedMultiValueMap<>();
            return;
        }

        MediaType mediaType = getContentType();
        if (MediaType.APPLICATION_FORM_URLENCODED.includes(mediaType)) {
            try {
                RequestBody body = getBody();
                String bodyString = body == null ? "" : body.string();
                mParameter = parseParameters(bodyString);
            } catch (Exception ignored) {
            }
        }
        if (mParameter == null) {
            mParameter = new LinkedMultiValueMap<>();
        }
        isParsedParameter = true;
    }

    @Nullable
    @Override
    public RequestBody getBody() {
        if (getMethod().allowBody()) {
            if (mRequest instanceof HttpEntityEnclosingRequest) {
                HttpEntityEnclosingRequest request = (HttpEntityEnclosingRequest) mRequest;
                HttpEntity entity = request.getEntity();
                if (entity == null) {
                    return null;
                }
                return new EntityToBody(entity);
            }
            return null;
        }
        throw new UnsupportedOperationException("This method does not allow body.");
    }

    @NonNull
    @Override
    public Session getValidSession() {
        Session session = getSession();
        if (session == null) {
            session = mSessionManager.createSession();
        } else if (session.isValid()) {
            session = mSessionManager.createSession();
        }

        setAttribute(REQUEST_CREATED_SESSION, session);
        return session;
    }

    @Override
    public Session getSession() {
        Object objSession = getAttribute(REQUEST_CREATED_SESSION);
        if (objSession instanceof Session) {
            return (Session) objSession;
        }

        List<Cookie> cookies = getCookies();
        if (cookies.isEmpty()) {
            return null;
        }

        String sessionId = null;
        for (Cookie cookie: cookies) {
            if (SESSION_NAME.equalsIgnoreCase(cookie.getName())) {
                sessionId = cookie.getValue();
                break;
            }
        }

        if (TextUtils.isEmpty(sessionId)) {
            return null;
        }

        Session session = null;
        try {
            session = mSessionManager.findSession(sessionId);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return session;
    }

    @Override
    public String changeSessionId() {
        Session session = getSession();
        if (session == null) {
            throw new IllegalStateException("No session associated with this request.");
        }
        mSessionManager.changeSessionId(session);
        return session.getId();
    }

    @Override
    public boolean isSessionValid() {
        Session session = getSession();
        return session != null && session.isValid();
    }

    @Nullable
    @Override
    public RequestDispatcher getRequestDispatcher(@NonNull String path) {
        return mHandler.getRequestDispatcher(this, path);
    }

    @Override
    public HttpContext getContext() {
        return mContext;
    }

    @Nullable
    @Override
    public Object getAttribute(@NonNull String id) {
        return mContext.getAttribute(id);
    }

    @Override
    public void setAttribute(@NonNull String id, @Nullable Object obj) {
        mContext.setAttribute(id, obj);
    }

    @Nullable
    @Override
    public Object removeAttribute(@NonNull String id) {
        return mContext.removeAttribute(id);
    }

    @NonNull
    private static MultiValueMap<String, String> parseParameters(@NonNull String input) {
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        StringTokenizer tokenizer = new StringTokenizer(input, "&");
        while (tokenizer.hasMoreElements()) {
            String element = tokenizer.nextToken();
            int end = element.indexOf("=");

            if (end > 0 && end < element.length() - 1) {
                String key = element.substring(0, end);
                String value = element.substring(end + 1);
                parameters.add(key, UrlCoder.urlDecode(value, Charsets.toCharset("utf-8")));
            }
        }
        return parameters;
    }

    private static class EntityToBody implements RequestBody {

        private HttpEntity mEntity;

        private EntityToBody(HttpEntity entity) {
            this.mEntity = entity;
        }

        @Override
        public String contentEncoding() {
            Header encoding = mEntity.getContentType();
            return encoding == null ? "" : encoding.getValue();
        }

        @Override
        public long length() {
            return mEntity.getContentLength();
        }

        @Nullable
        @Override
        public MediaType contentType() {
            Header header = mEntity.getContentType();
            if (header == null) {
                return null;
            }

            String contentType = header.getValue();
            return MediaType.valueOf(contentType);
        }

        @NonNull
        @Override
        public InputStream stream() throws IOException {
            InputStream stream = mEntity.getContent();
            if (contentEncoding().toLowerCase().contains("gzip")) {
                stream = new GZIPInputStream(stream);
            }
            return stream;
        }

        @NonNull
        @Override
        public String string() throws IOException {
            MimeType mimeType = contentType();
            Charset charset = mimeType == null ? null : mimeType.getCharset();

            if (charset == null) {
                return IOUtils.toString(stream());
            } else {
                return IOUtils.toString(stream(), charset);
            }
        }
    }
}