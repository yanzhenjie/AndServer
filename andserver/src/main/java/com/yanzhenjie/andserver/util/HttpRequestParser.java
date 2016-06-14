/*
 * Copyright © Yan Zhenjie. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing permissions and limitations under the License.
 */
package com.yanzhenjie.andserver.util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * <p>
 * Parsing {@link HttpRequest}.
 * </p>
 * Created on 2016/6/13.
 *
 * @author Yan Zhenjie.
 */
public class HttpRequestParser {

    /**
     * Parse params from {@link HttpRequest}.
     *
     * @param request {@link HttpRequest}.
     * @return parameter key-value pairs.
     * @throws IOException if it is a POST request IO exception might occur when get the data.
     */
    public static Map<String, String> parse(HttpRequest request) throws IOException {
        return parse(request, false);
    }

    /**
     * Parse params from {@link HttpRequest}.
     *
     * @param request        {@link HttpRequest}.
     * @param lowerCaseNames Whether to put all keys are converted to lowercase.
     * @return parameter key-value pairs.
     * @throws IOException if it is a POST request IO exception might occur when get the data.
     */
    public static Map<String, String> parse(HttpRequest request, boolean lowerCaseNames) throws IOException {
        String content = getContentForGet(request);
        return splitHttpParams(content, lowerCaseNames);
    }

    /**
     * Split http params.
     *
     * @param content        target content.
     * @param lowerCaseNames Whether to put all keys are converted to lowercase.
     * @return parameter key-value pairs.
     */
    public static Map<String, String> splitHttpParams(String content, boolean lowerCaseNames) {
        Map<String, String> paramMap = new HashMap<String, String>();
        StringTokenizer tokenizer = new StringTokenizer(content, "&");
        while (tokenizer.hasMoreElements()) {
            String keyValue = tokenizer.nextToken();
            int index = keyValue.indexOf("=");
            if (index > 0) {
                String key = keyValue.substring(0, index);
                if (lowerCaseNames)
                    key = key.toLowerCase(Locale.ENGLISH);
                paramMap.put(key, keyValue.substring(index + 1));
            }
        }
        return paramMap;
    }

    /**
     * The access request body.
     *
     * @param request {@link HttpRequest}.
     * @return string raw.
     * @throws IOException if it is a POST request IO exception might occur when get the data.
     */
    public static String getContent(HttpRequest request) throws IOException {
        if (isGetMethod(request)) {
            return getContentForGet(request);
        } else if (isPosterMethod(request)) {
            return getContentForPost(request);
        }
        return "";
    }

    /**
     * Get data from a {@code PostRequest}.
     *
     * @param request {@link HttpRequest}.
     * @return all the parameters in the request body, such as: name=yanzhenjie&sex = 1.
     * @throws IOException data read from the stream of time may be abnormal.
     */
    public static String getContentForPost(HttpRequest request) throws IOException {
        HttpEntity entity = ((HttpEntityEnclosingRequest) request).getEntity();
        return EntityUtils.toString(entity);
    }

    /**
     * Get data from a {@code GetRequest}.
     *
     * @param request {@link HttpRequest}.
     * @return url behind all the parameters, such as: name=yanzhenjie&sex = 1.
     */
    public static String getContentForGet(HttpRequest request) {
        String uri = request.getRequestLine().getUri();
        int index = uri.indexOf('?');
        return (index == -1) || (index + 1 >= uri.length()) ? "" : uri.substring(index + 1);
    }

    /**
     * If a GET request.
     *
     * @param request {@link HttpRequest}.
     * @return returns true, not return false.
     */
    public static boolean isGetMethod(HttpRequest request) {
        String method = request.getRequestLine().getMethod();
        return "GET".equalsIgnoreCase(method);
    }

    /**
     * If a POST request.
     *
     * @param request {@link HttpRequest}.
     * @return returns true, not return false.
     */
    public static boolean isPosterMethod(HttpRequest request) {
        String method = request.getRequestLine().getMethod();
        return "POST".equalsIgnoreCase(method);
    }

}
