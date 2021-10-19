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

import android.text.TextUtils;
import android.webkit.URLUtil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yanzhenjie.andserver.util.LinkedMultiValueMap;
import com.yanzhenjie.andserver.util.MultiValueMap;
import com.yanzhenjie.andserver.util.Patterns;
import com.yanzhenjie.andserver.util.UrlCoder;

import org.apache.commons.io.Charsets;

import java.net.URI;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Add the mPath to the URL, such as:
 * <pre>
 * Uri url = Uri.newBuilder("http://www.example.com/xx")
 *      .scheme("https")
 *      .port(8080)
 *      .path("yy")
 *      .query("abc", "123")
 *      .setFragment("article")
 *      .build();
 * ...
 * The real url is: <code>https://www.example.com:8080/xx/yy?abc=123#article</code>.
 * </pre>
 * <pre>
 * Uri url = Uri.newBuilder("http://www.example.com/xx/yy?abc=123")
 *      .setSegments("/aa/bb/cc")
 *      .setQuery("mln=456&ijk=789")
 *      .build();
 * ...
 * The real url is: <code>http://www.example.com/aa/bb/cc?mln=456&ijk=789</code>.
 * </pre>
 * <pre>
 * Uri url = Uri.newBuilder("http://www.example.com/user/photo/search?name=abc").build();
 * Uri newUrl = url.location("../../get?name=mln");
 * ...
 * The new url is: <code>http://www.example.com/get?name=abc</code>.
 * </pre>
 * Created by Zhenjie Yan on 2018/2/9.
 */
public class Uri implements Patterns {

    public static Builder newBuilder(String uri) {
        return new Builder(uri);
    }

    private final String mScheme;
    private final String mHost;
    private final int mPort;
    private final String mPath;
    private final String mQuery;
    private final String mFragment;

    private Uri(Builder builder) {
        this.mScheme = builder.mScheme;
        this.mHost = builder.mHost;
        this.mPort = builder.mPort;
        this.mPath = pathsToPath(builder.mPath);
        this.mQuery = parametersToQuery(builder.mQuery);
        this.mFragment = builder.mFragment;
    }

    @Nullable
    public String getScheme() {
        return mScheme;
    }

    @Nullable
    public String getHost() {
        return mHost;
    }

    public int getPort() {
        return mPort;
    }

    @NonNull
    public String getPath() {
        return mPath;
    }

    @NonNull
    public List<String> getPaths() {
        return pathToPaths(mPath);
    }

    @NonNull
    public String getQuery() {
        return mQuery;
    }

    @NonNull
    public MultiValueMap<String, String> getParams() {
        return queryToParameters(mQuery);
    }

    @Nullable
    public String getFragment() {
        return mFragment;
    }

    @NonNull
    public Builder builder() {
        return new Builder(toString());
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (!TextUtils.isEmpty(mScheme)) {
            builder.append(mScheme).append(":");
        }

        if (!TextUtils.isEmpty(mHost) && mPort > 0) {
            builder.append("//").append(mHost).append(":").append(mPort);
        }

        if (!TextUtils.isEmpty(mPath)) {
            builder.append(mPath);
        }

        if (!TextUtils.isEmpty(mQuery)) {
            builder.append("?").append(mQuery);
        }

        if (!TextUtils.isEmpty(mFragment)) {
            builder.append("#").append(mFragment);
        }

        return builder.toString();
    }

    @Nullable
    public Uri location(@Nullable String location) {
        if (TextUtils.isEmpty(location)) {
            return null;
        }

        if (URLUtil.isNetworkUrl(location)) {
            return newBuilder(location).build();
        }

        URI newUri = URI.create(location);
        if (location.startsWith("/")) {
            return builder().setPath(newUri.getPath())
                .setQuery(newUri.getQuery())
                .setFragment(newUri.getFragment())
                .build();
        } else if (location.contains("../")) {
            List<String> oldPathList = pathToPaths(getPath());
            List<String> newPathList = pathToPaths(newUri.getPath());

            int start = newPathList.lastIndexOf("..");
            newPathList = newPathList.subList(start + 1, newPathList.size());
            if (!oldPathList.isEmpty()) {
                oldPathList = oldPathList.subList(0, oldPathList.size() - start - 2);
                oldPathList.addAll(newPathList);
                String path = TextUtils.join("/", oldPathList);
                return builder().setPath(path).setQuery(newUri.getQuery()).setFragment(newUri.getFragment()).build();
            }
            String path = TextUtils.join("/", newPathList);
            return builder().setPath(path).setQuery(newUri.getQuery()).setFragment(newUri.getFragment()).build();
        } else {
            List<String> oldPathList = pathToPaths(getPath());
            oldPathList.addAll(pathToPaths(newUri.getPath()));
            String path = TextUtils.join("/", oldPathList);
            return builder().setPath(path).setQuery(newUri.getQuery()).setFragment(newUri.getFragment()).build();
        }
    }

    public static class Builder {

        private String mScheme;
        private String mHost;
        private int mPort;
        private List<String> mPath;
        private MultiValueMap<String, String> mQuery;
        private String mFragment;

        private Builder(@NonNull String url) {
            URI uri = URI.create(url);

            this.mScheme = uri.getScheme();
            this.mHost = uri.getHost();
            this.mPort = uri.getPort();
            String path = uri.getPath();
            this.mPath = pathToPaths(path);
            String query = uri.getRawQuery();
            this.mQuery = queryToParameters(query);
            this.mFragment = uri.getFragment();
        }

        public Builder setScheme(@Nullable String scheme) {
            this.mScheme = scheme;
            return this;
        }

        public Builder setHost(@Nullable String host) {
            this.mHost = host;
            return this;
        }

        public Builder setPort(int port) {
            this.mPort = port;
            return this;
        }

        public Builder addPath(int value) {
            return addPath(Integer.toString(value));
        }

        public Builder addPath(long value) {
            return addPath(Long.toString(value));
        }

        public Builder addPath(boolean value) {
            return addPath(Boolean.toString(value));
        }

        public Builder addPath(char value) {
            return addPath(String.valueOf(value));
        }

        public Builder addPath(double value) {
            return addPath(Double.toString(value));
        }

        public Builder addPath(float value) {
            return addPath(Float.toString(value));
        }

        public Builder addPath(@NonNull CharSequence path) {
            mPath.add(path.toString());
            return this;
        }

        public Builder addPath(@NonNull String path) {
            mPath.add(path);
            return this;
        }

        public Builder setPath(@NonNull String path) {
            mPath = pathToPaths(path);
            return this;
        }

        public Builder clearPath() {
            mPath.clear();
            return this;
        }

        public Builder addQuery(@NonNull String key, int value) {
            return addQuery(key, Integer.toString(value));
        }

        public Builder addQuery(@NonNull String key, long value) {
            return addQuery(key, Long.toString(value));
        }

        public Builder addQuery(@NonNull String key, boolean value) {
            return addQuery(key, Boolean.toString(value));
        }

        public Builder addQuery(@NonNull String key, char value) {
            return addQuery(key, String.valueOf(value));
        }

        public Builder addQuery(@NonNull String key, double value) {
            return addQuery(key, Double.toString(value));
        }

        public Builder addQuery(@NonNull String key, float value) {
            return addQuery(key, Float.toString(value));
        }

        public Builder addQuery(@NonNull String key, short value) {
            return addQuery(key, Integer.toString(value));
        }

        public Builder addQuery(@NonNull String key, @NonNull CharSequence value) {
            mQuery.add(key, value.toString());
            return this;
        }

        public Builder addQuery(@NonNull String key, @NonNull String value) {
            mQuery.add(key, value);
            return this;
        }

        public Builder addQuery(@NonNull String key, @NonNull List<String> values) {
            for (String value: values) {
                mQuery.add(key, value);
            }
            return this;
        }

        public Builder setQuery(@Nullable String query) {
            mQuery = queryToParameters(query);
            return this;
        }

        public Builder setQuery(@NonNull MultiValueMap<String, String> query) {
            mQuery = query;
            return this;
        }

        public Builder removeQuery(@NonNull String key) {
            mQuery.remove(key);
            return this;
        }

        public Builder clearQuery() {
            mQuery.clear();
            return this;
        }

        public Builder setFragment(@Nullable String fragment) {
            this.mFragment = fragment;
            return this;
        }

        public Uri build() {
            return new Uri(this);
        }
    }

    public static List<String> pathToPaths(String path) {
        List<String> pathList = new LinkedList<>();
        if (TextUtils.isEmpty(path)) {
            return pathList;
        }

        while (path.contains("//")) {
            path = path.replace("//", "/");
        }

        while (path.contains("/")) {
            if (path.startsWith("/")) {
                pathList.add("");
                path = path.substring(1);
            } else {
                int index = path.indexOf("/");
                pathList.add(path.substring(0, index));
                path = path.substring(index + 1);
            }

            if (!path.contains("/")) {
                pathList.add(path);
            }
        }
        return pathList;
    }

    public static MultiValueMap<String, String> queryToParameters(String query) {
        MultiValueMap<String, String> valueMap = new LinkedMultiValueMap<>();
        if (!TextUtils.isEmpty(query)) {
            if (query.startsWith("?")) {
                query = query.substring(1);
            }

            StringTokenizer tokenizer = new StringTokenizer(query, "&");
            while (tokenizer.hasMoreElements()) {
                String element = tokenizer.nextToken();
                int end = element.indexOf("=");

                if (end > 0 && end < element.length() - 1) {
                    String key = element.substring(0, end);
                    String value = element.substring(end + 1);
                    valueMap.add(key, UrlCoder.urlDecode(value, Charsets.toCharset("utf-8")));
                }
            }
        }
        return valueMap;
    }

    public static String pathsToPath(List<String> pathList) {
        if (pathList == null || pathList.isEmpty()) {
            return "";
        }

        StringBuilder builder = new StringBuilder();
        for (String path: pathList) {
            builder.append("/").append(path);
        }

        String path = builder.toString();
        while (path.contains("//")) {
            path = path.replace("//", "/");
        }
        return path;
    }

    public static String parametersToQuery(MultiValueMap<String, String> params) {
        StringBuilder builder = new StringBuilder();
        Iterator<Map.Entry<String, List<String>>> iterator = params.entrySet().iterator();
        if (iterator.hasNext()) {
            Map.Entry<String, List<String>> param = iterator.next();
            String key = param.getKey();
            List<String> valueList = param.getValue();
            if (valueList != null && !valueList.isEmpty()) {
                for (String value: valueList) {
                    builder.append(key).append("=").append(UrlCoder.urlEncode(value, "utf-8"));
                }
            } else {
                builder.append(key).append("=");
            }
        }

        while (iterator.hasNext()) {
            Map.Entry<String, List<String>> param = iterator.next();
            String key = param.getKey();
            List<String> valueList = param.getValue();
            if (valueList != null && !valueList.isEmpty()) {
                for (String value: valueList) {
                    builder.append("&").append(key).append("=").append(UrlCoder.urlEncode(value, "utf-8"));
                }
            } else {
                builder.append("&").append(key).append("=");
            }
        }
        return builder.toString();
    }
}