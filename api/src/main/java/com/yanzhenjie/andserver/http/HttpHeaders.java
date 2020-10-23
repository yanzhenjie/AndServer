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

/**
 * Created by Zhenjie Yan on 2018/9/7.
 */
public interface HttpHeaders {

    /**
     * RFC 2616 (HTTP/1.1) Section 14.1
     */
    String ACCEPT = "Accept";

    /**
     * RFC 2616 (HTTP/1.1) Section 14.2
     */
    String ACCEPT_CHARSET = "Accept-Charset";

    /**
     * Access-Control-Allow-Credentials.
     */
    String Access_Control_Allow_Credentials = "Access-Control-Allow-Credentials";

    /**
     * Access-Control-Allow-Headers.
     */
    String Access_Control_Allow_Headers = "Access-Control-Allow-Headers";

    /**
     * Access-Control-Allow-Methods.
     */
    String Access_Control_Allow_Methods = "Access-Control-Allow-Methods";

    /**
     * Access-Control-Allow-Origin.
     */
    String Access_Control_Allow_Origin = "Access-Control-Allow-Origin";

    /**
     * Access-Control-Expose-Headers.
     */
    String Access_Control_Expose_Headers = "Access-Control-Expose-Headers";

    /**
     * Access-Control-Max-Age.
     */
    String Access_Control_Max_Age = "Access-Control-Max-Age";

    /**
     * Access-Control-Request-Headers.
     */
    String Access_Control_Request_Headers = "Access-Control-Request-Headers";

    /**
     * Access-Control-Request-Method.
     */
    String Access_Control_Request_Method = "Access-Control-Request-Method";

    /**
     * RFC 2616 (HTTP/1.1) Section 14.3
     */
    String ACCEPT_ENCODING = "Accept-Encoding";

    /**
     * RFC 2616 (HTTP/1.1) Section 14.4
     */
    String ACCEPT_LANGUAGE = "Accept-Language";

    /**
     * RFC 2616 (HTTP/1.1) Section 14.5
     */
    String ACCEPT_RANGES = "Accept-Ranges";

    /**
     * RFC 2616 (HTTP/1.1) Section 14.6
     */
    String AGE = "Age";

    /**
     * RFC 1945 (HTTP/1.0) Section 10.1, RFC 2616 (HTTP/1.1) Section 14.7
     */
    String ALLOW = "Allow";

    /**
     * RFC 1945 (HTTP/1.0) Section 10.2, RFC 2616 (HTTP/1.1) Section 14.8
     */
    String AUTHORIZATION = "Authorization";

    /**
     * RFC 2616 (HTTP/1.1) Section 14.9
     */
    String CACHE_CONTROL = "Cache-Control";

    /**
     * RFC 2616 (HTTP/1.1) Section 14.10
     */
    String CONNECTION = "Connection";

    /**
     * RFC 1945 (HTTP/1.0) Section 10.3, RFC 2616 (HTTP/1.1) Section 14.11
     */
    String CONTENT_ENCODING = "Content-Encoding";

    /**
     * RFC 2616 (HTTP/1.1) Section 14.12
     */
    String CONTENT_LANGUAGE = "Content-Language";

    /**
     * RFC 1945 (HTTP/1.0) Section 10.4, RFC 2616 (HTTP/1.1) Section 14.13
     */
    String CONTENT_LENGTH = "Content-Length";

    /**
     * RFC 2616 (HTTP/1.1) Section 14.14
     */
    String CONTENT_LOCATION = "Content-Location";

    /**
     * RFC 2616 (HTTP/1.1) Section 14.15
     */
    String CONTENT_MD5 = "Content-MD5";

    /**
     * RFC 2616 (HTTP/1.1) Section 14.16
     */
    String CONTENT_RANGE = "Content-Range";

    /**
     * RFC 1945 (HTTP/1.0) Section 10.5, RFC 2616 (HTTP/1.1) Section 14.17
     */
    String CONTENT_TYPE = "Content-Type";

    /**
     * RFC 6265 (HTTP/1.0) Section 4.2
     */
    String COOKIE = "Cookie";

    /**
     * RFC 1945 (HTTP/1.0) Section 10.6, RFC 2616 (HTTP/1.1) Section 14.18
     */
    String DATE = "Date";

    /**
     * RFC 2518 (WevDAV) Section 9.1
     */
    String DAV = "Dav";

    /**
     * RFC 2518 (WevDAV) Section 9.2
     */
    String DEPTH = "Depth";

    /**
     * RFC 2518 (WevDAV) Section 9.3
     */
    String DESTINATION = "Destination";

    /**
     * RFC 2616 (HTTP/1.1) Section 14.19
     */
    String ETAG = "ETag";

    /**
     * RFC 2616 (HTTP/1.1) Section 14.20
     */
    String EXPECT = "Expect";

    /**
     * RFC 1945 (HTTP/1.0) Section 10.7, RFC 2616 (HTTP/1.1) Section 14.21
     */
    String EXPIRES = "Expires";

    /**
     * RFC 1945 (HTTP/1.0) Section 10.8, RFC 2616 (HTTP/1.1) Section 14.22
     */
    String FROM = "From";

    /**
     * RFC 2616 (HTTP/1.1) Section 14.23
     */
    String HOST = "Host";

    /**
     * RFC 2518 (WevDAV) Section 9.4
     */
    String IF = "If";

    /**
     * RFC 2616 (HTTP/1.1) Section 14.24
     */
    String IF_MATCH = "If-Match";

    /**
     * RFC 1945 (HTTP/1.0) Section 10.9, RFC 2616 (HTTP/1.1) Section 14.25
     */
    String IF_MODIFIED_SINCE = "If-Modified-Since";

    /**
     * RFC 2616 (HTTP/1.1) Section 14.26
     */
    String IF_NONE_MATCH = "If-None-Match";

    /**
     * RFC 2616 (HTTP/1.1) Section 14.27
     */
    String IF_RANGE = "If-Range";

    /**
     * RFC 2616 (HTTP/1.1) Section 14.28
     */
    String IF_UNMODIFIED_SINCE = "If-Unmodified-Since";

    /**
     * RFC 1945 (HTTP/1.0) Section 10.10, RFC 2616 (HTTP/1.1) Section 14.29
     */
    String LAST_MODIFIED = "Last-Modified";

    /**
     * RFC 1945 (HTTP/1.0) Section 10.11, RFC 2616 (HTTP/1.1) Section 14.30
     */
    String LOCATION = "Location";

    /**
     * RFC 2518 (WevDAV) Section 9.5
     */
    String LOCK_TOKEN = "Lock-Token";

    /**
     * RFC 2616 (HTTP/1.1) Section 14.31
     */
    String MAX_FORWARDS = "Max-Forwards";

    /**
     * Origin.
     */
    String ORIGIN = "Origin";

    /**
     * RFC 2518 (WevDAV) Section 9.6
     */
    String OVERWRITE = "Overwrite";

    /**
     * RFC 1945 (HTTP/1.0) Section 10.12, RFC 2616 (HTTP/1.1) Section 14.32
     */
    String PRAGMA = "Pragma";

    /**
     * RFC 2616 (HTTP/1.1) Section 14.33
     */
    String PROXY_AUTHENTICATE = "Proxy-Authenticate";

    /**
     * RFC 2616 (HTTP/1.1) Section 14.34
     */
    String PROXY_AUTHORIZATION = "Proxy-Authorization";

    /**
     * RFC 2616 (HTTP/1.1) Section 14.35
     */
    String RANGE = "Range";

    /**
     * RFC 1945 (HTTP/1.0) Section 10.13, RFC 2616 (HTTP/1.1) Section 14.36
     */
    String REFERER = "Referer";

    /**
     * RFC 2616 (HTTP/1.1) Section 14.37
     */
    String RETRY_AFTER = "Retry-After";

    /**
     * RFC 1945 (HTTP/1.0) Section 10.14, RFC 2616 (HTTP/1.1) Section 14.38
     */
    String SERVER = "Server";

    /**
     * RFC 6265 (HTTP/1.0) Section 4.1， RFC 2109 (Http/1.1) Section 4.2.2
     */
    String SET_COOKIE = "Set-Cookie";

    /**
     * RFC 2518 (WevDAV) Section 9.7
     */
    String STATUS_URI = "Status-URI";

    /**
     * RFC 2616 (HTTP/1.1) Section 14.39
     */
    String TE = "TE";

    /**
     * RFC 2518 (WevDAV) Section 9.8
     */
    String TIMEOUT = "Timeout";

    /**
     * RFC 2616 (HTTP/1.1) Section 14.40
     */
    String TRAILER = "Trailer";

    /**
     * RFC 2616 (HTTP/1.1) Section 14.41
     */
    String TRANSFER_ENCODING = "Transfer-Encoding";

    /**
     * RFC 2616 (HTTP/1.1) Section 14.42
     */
    String UPGRADE = "Upgrade";

    /**
     * RFC 1945 (HTTP/1.0) Section 10.15, RFC 2616 (HTTP/1.1) Section 14.43
     */
    String USER_AGENT = "User-Agent";

    /**
     * RFC 2616 (HTTP/1.1) Section 14.44
     */
    String VARY = "Vary";

    /**
     * RFC 2616 (HTTP/1.1) Section 14.45
     */
    String VIA = "Via";

    /**
     * RFC 2616 (HTTP/1.1) Section 14.46
     */
    String WARNING = "Warning";

    /**
     * RFC 1945 (HTTP/1.0) Section 10.16, RFC 2616 (HTTP/1.1) Section 14.47
     */
    String WWW_AUTHENTICATE = "WWW-Authenticate";
}