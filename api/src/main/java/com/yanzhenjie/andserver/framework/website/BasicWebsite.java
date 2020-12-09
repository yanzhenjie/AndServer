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
package com.yanzhenjie.andserver.framework.website;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.yanzhenjie.andserver.http.HttpRequest;
import com.yanzhenjie.andserver.util.Assert;
import com.yanzhenjie.andserver.util.MultiValueMap;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Created by Zhenjie Yan on 2018/9/6.
 */
public abstract class BasicWebsite extends Website {

    public static final String DEFAULT_INDEX = "index.html";

    private final String mIndexFileName;

    public BasicWebsite() {
        this(DEFAULT_INDEX);
    }

    /**
     * Create a website object.
     *
     * @param indexFileName the default file name for each directory, e.g. index.html.
     */
    public BasicWebsite(@NonNull String indexFileName) {
        Assert.isTrue(!TextUtils.isEmpty(indexFileName), "The indexFileName cannot be empty.");
        this.mIndexFileName = indexFileName;
    }

    @Override
    public String getETag(@NonNull HttpRequest request) throws Throwable {
        return null;
    }

    @Override
    public long getLastModified(@NonNull HttpRequest request) throws Throwable {
        return -1;
    }

    /**
     * Get the name of the indexFile.
     *
     * @return file name, does not include the path.
     */
    @NonNull
    protected final String getIndexFileName() {
        return mIndexFileName;
    }

    /**
     * Add the '/' to the beginning.
     *
     * @param target target string.
     *
     * @return rule result.
     */
    protected String addStartSlash(@NonNull String target) {
        if (!target.startsWith(File.separator)) {
            target = File.separator + target;
        }
        return target;
    }

    /**
     * Add '/' at the ending.
     *
     * @param target target string.
     *
     * @return rule result.
     */
    protected String addEndSlash(@NonNull String target) {
        if (!target.endsWith(File.separator)) {
            target = target + File.separator;
        }
        return target;
    }

    /**
     * Remove '/' at the beginning.
     *
     * @param target target string.
     *
     * @return rule result.
     */
    protected String trimStartSlash(@NonNull String target) {
        while (target.startsWith(File.separator))
            target = target.substring(1);
        return target;
    }

    /**
     * Remove '/' at the ending.
     *
     * @param target target string.
     *
     * @return rule result.
     */
    protected String trimEndSlash(@NonNull String target) {
        while (target.endsWith(File.separator))
            target = target.substring(0, target.length() - 1);
        return target;
    }

    /**
     * Remove the '/' at the beginning and ending.
     *
     * @param target target string.
     *
     * @return rule result.
     */
    protected String trimSlash(@NonNull String target) {
        target = trimStartSlash(target);
        target = trimEndSlash(target);
        return target;
    }

    protected String queryString(HttpRequest request) {
        MultiValueMap<String, String> query = request.getQuery();
        if (query.isEmpty()) {
            return "";
        }

        StringBuilder queryString = new StringBuilder();
        for (Map.Entry<String, List<String>> entry: query.entrySet()) {
            String key = entry.getKey();
            List<String> values = entry.getValue();
            if (values != null && !values.isEmpty()) {
                for (int i = 0; i < values.size(); i++) {
                    queryString.append("&")
                        .append(key)
                        .append("=")
                        .append(values.get(i));
                }
            }
        }
        if (queryString.length() > 0) {
            queryString.deleteCharAt(0);
        }
        return queryString.toString();
    }
}