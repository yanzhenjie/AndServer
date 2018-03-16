/*
 * Copyright © 2016 Yan Zhenjie.
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
package com.yanzhenjie.andserver.util;

import android.text.TextUtils;

import com.yanzhenjie.andserver.RequestMethod;
import com.yanzhenjie.andserver.upload.HttpUploadContext;

import org.apache.commons.fileupload.FileUploadBase;
import org.apache.httpcore.Header;
import org.apache.httpcore.HttpEntity;
import org.apache.httpcore.HttpEntityEnclosingRequest;
import org.apache.httpcore.HttpRequest;
import org.apache.httpcore.util.EntityUtils;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * <p>
 * Parsing {@link HttpRequest}.
 * </p>
 * Created by Yan Zhenjie on 2016/6/13.
 */
public class HttpRequestParser {

    public static final String CHARSET_UTF8 = "utf-8";

    /**
     * Parse params from {@link HttpRequest}.
     *
     * @param request {@link HttpRequest}.
     * @return parameter key-value pairs.
     * @throws IOException if it is a POST request IO exception might occur when get the data.
     */
    public static Map<String, String> parseParams(HttpRequest request) throws IOException {
        return parseParams(request, false);
    }

    /**
     * Parse params from {@link HttpRequest}.
     *
     * @param request        {@link HttpRequest}.
     * @param lowerCaseNames key to lowercase.
     * @return parameter key-value pairs.
     * @throws IOException if it is a POST request IO exception might occur when get the data.
     */
    public static Map<String, String> parseParams(HttpRequest request, boolean lowerCaseNames) throws IOException {
        String content = getContent(request);
        return splitHttpParams(content, lowerCaseNames);
    }

    /**
     * Split http params.
     *
     * @param content        target content.
     * @param lowerCaseNames key to lowercase.
     * @return parameter key-value pairs.
     */
    public static Map<String, String> splitHttpParams(String content, boolean lowerCaseNames) {
        Map<String, String> paramMap = new HashMap<>();
        StringTokenizer tokenizer = new StringTokenizer(content, "&");
        while (tokenizer.hasMoreElements()) {
            String keyValue = tokenizer.nextToken();
            int index = keyValue.indexOf("=");
            if (index > 0) {
                String key = keyValue.substring(0, index);
                if (lowerCaseNames)
                    key = key.toLowerCase(Locale.ENGLISH);
                paramMap.put(key, UrlCoder.urlDecode(keyValue.substring(index + 1), CHARSET_UTF8));
            }
        }
        return paramMap;
    }

    /**
     * Obtain the contents of the Request.
     */
    public static String getContent(HttpRequest request) throws IOException {
        if (isAllowRequestBody(request)) {
            return getContentFromBody(request);
        } else {
            return getContentFromUri(request);
        }
    }

    /**
     * Obtain the contents of the RequestBody.
     */
    public static String getContentFromBody(HttpRequest request) throws IOException {
        HttpEntity entity = ((HttpEntityEnclosingRequest) request).getEntity();
        String charset = parseHeadValue(entity.getContentType().getValue(), "charset", CHARSET_UTF8);
        return EntityUtils.toString(entity, charset);
    }

    /**
     * Obtain the parameters from the URI path.
     */
    public static String getContentFromUri(HttpRequest request) {
        String uri = request.getRequestLine().getUri();
        int index = uri.indexOf('?');
        return (index == -1) || (index + 1 >= uri.length()) ? "" : uri.substring(index + 1);
    }

    /**
     * Obtain the path of the current request.
     */
    public static String getRequestPath(HttpRequest request) {
        String uriPath = request.getRequestLine().getUri();
        int index = uriPath.indexOf("?");
        if (index != -1) {
            uriPath = uriPath.substring(0, index);
        } else {
            index = uriPath.indexOf("#");
            if (index != -1) {
                uriPath = uriPath.substring(0, index);
            }
        }
        String[] pathArray = uriPath.split("/");
        if (pathArray.length > 1) {
            List<String> pathList = new ArrayList<>();
            for (String path : pathArray) {
                path = UrlCoder.urlDecode(path, "utf-8");
                pathList.add(path);
            }
            uriPath = TextUtils.join("/", pathList);
        }
        return uriPath;
    }

    /**
     * Is this a request that allows a body?
     */
    public static boolean isAllowRequestBody(HttpRequest request) {
        return getRequestMethod(request).allowRequestBody();
    }

    /**
     * Get the RequestMethod of Request.
     */
    public static RequestMethod getRequestMethod(HttpRequest request) {
        String method = request.getRequestLine().getMethod();
        return RequestMethod.reverse(method);
    }

    /**
     * Whether to allow the RequestBody with the request.
     */
    public static boolean isMultipartContentRequest(HttpRequest request) {
        if (!(request instanceof HttpEntityEnclosingRequest)) return false;
        HttpEntityEnclosingRequest enclosingRequest = (HttpEntityEnclosingRequest) request;
        return isAllowRequestBody(request) && FileUploadBase.isMultipartContent(new HttpUploadContext(enclosingRequest));
    }

    /**
     * Parse the request for the specified time header.
     */
    public static long parseDateHeader(HttpRequest request, String headerName) {
        Header header = request.getFirstHeader(headerName);
        if (header != null) {
            String dateValue = header.getValue();
            try {
                return DateUtils.parseGMTToMillis(dateValue);
            } catch (ParseException ex) {
                int separatorIndex = dateValue.indexOf(';');
                if (separatorIndex != -1) {
                    String datePart = dateValue.substring(0, separatorIndex);
                    try {
                        return DateUtils.parseGMTToMillis(datePart);
                    } catch (ParseException ignored) {
                    }
                }
            }
        }
        return -1;
    }

    /**
     * A value of the header information.
     *
     * @param content      like {@code text/html;charset=utf-8}.
     * @param key          like {@code charset}.
     * @param defaultValue list {@code utf-8}.
     * @return If you have a value key, you will return the parsed value if you don't return the default value.
     */
    public static String parseHeadValue(String content, String key, String defaultValue) {
        if (!TextUtils.isEmpty(content) && !TextUtils.isEmpty(key)) {
            StringTokenizer stringTokenizer = new StringTokenizer(content, ";");
            while (stringTokenizer.hasMoreElements()) {
                String valuePair = stringTokenizer.nextToken();
                int index = valuePair.indexOf('=');
                if (index > 0) {
                    String name = valuePair.substring(0, index).trim();
                    if (key.equalsIgnoreCase(name)) {
                        defaultValue = valuePair.substring(index + 1).trim();
                        break;
                    }
                }
            }
        }
        return defaultValue;
    }

}
