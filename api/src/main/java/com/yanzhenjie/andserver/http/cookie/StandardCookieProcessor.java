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

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.apache.httpcore.Header;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.TimeZone;

/**
 * Created by Zhenjie Yan on 2018/7/27.
 */
public class StandardCookieProcessor implements CookieProcessor {

    private static final String COOKIE_DATE_PATTERN = "EEE, dd-MMM-yyyy HH:mm:ss z";

    private static final ThreadLocal<DateFormat> COOKIE_DATE_FORMAT = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            DateFormat df = new SimpleDateFormat(COOKIE_DATE_PATTERN, Locale.US);
            df.setTimeZone(TimeZone.getTimeZone("GMT"));
            return df;
        }
    };

    private static final String ANCIENT_DATE = COOKIE_DATE_FORMAT.get().format(new Date(10000));
    private static final BitSet DOMAIN_VALID = new BitSet(128);

    static {
        for (char c = '0'; c <= '9'; c++) {
            DOMAIN_VALID.set(c);
        }
        for (char c = 'a'; c <= 'z'; c++) {
            DOMAIN_VALID.set(c);
        }
        for (char c = 'A'; c <= 'Z'; c++) {
            DOMAIN_VALID.set(c);
        }
        DOMAIN_VALID.set('.');
        DOMAIN_VALID.set('-');
    }

    @NonNull
    @Override
    public List<Cookie> parseCookieHeader(@Nullable Header[] headers) {
        if (headers == null || headers.length == 0) {
            return Collections.emptyList();
        }

        List<Cookie> cookieList = new ArrayList<>();
        for (Header header: headers) {
            String name = header.getName();
            if ("Cookie".equalsIgnoreCase(name)) {
                String headerValue = header.getValue();
                parserCookieValue(headerValue, cookieList);
            }
        }
        return cookieList;
    }

    private void parserCookieValue(String headerValue, List<Cookie> cookieList) {
        StringTokenizer tokenizer = new StringTokenizer(headerValue, ";");
        while (tokenizer.hasMoreTokens()) {
            String segment = tokenizer.nextToken();
            int split = segment.indexOf("=");
            int valueIndex = split + 1;
            if (split > 0 && valueIndex < segment.length()) {
                String name = segment.substring(0, split).trim();
                String value = segment.substring(valueIndex, segment.length()).trim();
                cookieList.add(new Cookie(name, value));
            }
        }
    }

    @NonNull
    @Override
    public String generateHeader(@NonNull Cookie cookie) {
        // Can't use StringBuilder due to DateFormat.
        StringBuffer header = new StringBuffer();
        header.append(cookie.getName());
        header.append('=');
        String value = cookie.getValue();
        if (!TextUtils.isEmpty(value)) {
            validateCookieValue(value);
            header.append(value);
        }

        // RFC 6265 prefers Max-Age to Expires but... (see below).
        int maxAge = cookie.getMaxAge();
        if (maxAge > -1) {
            // Negative Max-Age is equivalent to no Max-Age.
            header.append("; Max-Age=");
            header.append(maxAge);

            // Microsoft IE and Microsoft Edge don't understand Max-Age so send
            // expires as well. Without this, persistent cookies fail with those
            // browsers. See http://tomcat.markmail.org/thread/g6sipbofsjossacn.

            // Wdy, DD-Mon-YY HH:MM:SS GMT (Expires Netscape format).
            header.append("; Expires=");
            // To expire immediately we need to set the time in past.
            if (maxAge == 0) {
                header.append(ANCIENT_DATE);
            } else {
                Date date = new Date(System.currentTimeMillis() + maxAge * 1000L);
                COOKIE_DATE_FORMAT.get().format(date, header, new FieldPosition(0));
            }
        }

        String domain = cookie.getDomain();
        if (domain != null && domain.length() > 0) {
            validateDomain(domain);
            header.append("; Domain=");
            header.append(domain);
        }

        String path = cookie.getPath();
        if (path != null && path.length() > 0) {
            validatePath(path);
            header.append("; Path=");
            header.append(path);
        }

        if (cookie.getSecure()) {
            header.append("; Secure");
        }

        if (cookie.isHttpOnly()) {
            header.append("; HttpOnly");
        }

        return header.toString();
    }


    private void validateCookieValue(String value) {
        int start = 0;
        int end = value.length();

        if (end > 1 && value.charAt(0) == '"' && value.charAt(end - 1) == '"') {
            start = 1;
            end--;
        }

        char[] chars = value.toCharArray();
        for (int i = start; i < end; i++) {
            char c = chars[i];
            if (c < 0x21 || c == 0x22 || c == 0x2c || c == 0x3b || c == 0x5c || c == 0x7f) {
                String message = String.format("The cookie's value [%1$s] is invalid.", value);
                throw new IllegalArgumentException(message);
            }
        }
    }


    private void validateDomain(String domain) {
        int i = 0;
        int prev = -1;
        int cur = -1;
        char[] chars = domain.toCharArray();
        while (i < chars.length) {
            prev = cur;
            cur = chars[i];
            if (!DOMAIN_VALID.get(cur)) {
                String message = String.format("The cookie's domain [%1$s] is invalid.", domain);
                throw new IllegalArgumentException(message);
            }
            // labels must start with a letter or number.
            if ((prev == '.' || prev == -1) && (cur == '.' || cur == '-')) {
                String message = String.format("The cookie's domain [%1$s] is invalid.", domain);
                throw new IllegalArgumentException(message);
            }
            // labels must end with a letter or number.
            if (prev == '-' && cur == '.') {
                String message = String.format("The cookie's domain [%1$s] is invalid.", domain);
                throw new IllegalArgumentException(message);
            }
            i++;
        }
        // domain must end with a label.
        if (cur == '.' || cur == '-') {
            String message = String.format("The cookie's domain [%1$s] is invalid.", domain);
            throw new IllegalArgumentException(message);
        }
    }


    private void validatePath(String path) {
        char[] chars = path.toCharArray();

        for (int i = 0; i < chars.length; i++) {
            char ch = chars[i];
            if (ch < 0x20 || ch > 0x7E || ch == ';') {
                String message = String.format("The cookie's path [%1$s] is invalid.", path);
                throw new IllegalArgumentException(message);
            }
        }
    }
}