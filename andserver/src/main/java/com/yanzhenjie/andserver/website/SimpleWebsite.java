/*
 * Copyright © Yan Zhenjie. All Rights Reserved
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
package com.yanzhenjie.andserver.website;

import com.yanzhenjie.andserver.View;

import org.apache.httpcore.HttpException;
import org.apache.httpcore.HttpRequest;
import org.apache.httpcore.HttpResponse;
import org.apache.httpcore.protocol.HttpContext;

import java.io.File;
import java.io.IOException;

/**
 * <p>Basic website.</p>
 * Created by Yan Zhenjie on 2017/3/16.
 */
public abstract class SimpleWebsite implements WebSite {

    protected static final String INDEX_FILE_PATH = "/index.html";

    /**
     * Remove the '/' at the beginning.
     *
     * @param target target string.
     * @return rule result.
     */
    public static String addStartSlash(String target) {
        if (!target.startsWith(File.separator)) target = File.separator + target;
        return target;
    }

    /**
     * Remove the '/' at the beginning.
     *
     * @param target target string.
     * @return rule result.
     */
    public static String addEndSlash(String target) {
        if (!target.endsWith(File.separator)) target = target + File.separator;
        return target;
    }

    /**
     * Remove the '/' at the beginning.
     *
     * @param target target string.
     * @return rule result.
     */
    public static String trimStartSlash(String target) {
        while (target.startsWith(File.separator)) target = target.substring(1);
        return target;
    }

    /**
     * Remove the '/' at the end of the string.
     *
     * @param target target string.
     * @return rule result.
     */
    public static String trimEndSlash(String target) {
        while (target.endsWith(File.separator)) target = target.substring(0, target.length() - 1);
        return target;
    }

    /**
     * Remove the '/' at the beginning and end of the string.
     *
     * @param target target string.
     * @return rule result.
     */
    public static String trimSlash(String target) {
        target = trimStartSlash(target);
        target = trimEndSlash(target);
        return target;
    }

    /**
     * Generates a registration name based on the file path.
     *
     * @param filePath file path.
     * @return registration name.
     */
    public static String getHttpPath(String filePath) {
        if (!filePath.startsWith(File.separator))
            filePath = File.separator + filePath;
        return filePath;
    }

    @Override
    public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
        View view = handle(request);
        response.setStatusCode(view.getHttpCode());
        response.setEntity(view.getHttpEntity());
    }

    protected abstract View handle(HttpRequest request) throws HttpException, IOException;
}
