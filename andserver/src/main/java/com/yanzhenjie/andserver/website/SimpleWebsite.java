/*
 * Copyright © 2017 Yan Zhenjie.
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

import com.yanzhenjie.andserver.view.View;
import com.yanzhenjie.andserver.exception.NotFoundException;

import org.apache.httpcore.HttpException;
import org.apache.httpcore.HttpRequest;
import org.apache.httpcore.HttpResponse;
import org.apache.httpcore.protocol.HttpContext;

import java.io.File;
import java.io.IOException;

import static com.yanzhenjie.andserver.util.HttpRequestParser.getRequestPath;

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
    protected String addStartSlash(String target) {
        if (!target.startsWith(File.separator)) target = File.separator + target;
        return target;
    }

    /**
     * Remove the '/' at the beginning.
     *
     * @param target target string.
     * @return rule result.
     */
    protected String addEndSlash(String target) {
        if (!target.endsWith(File.separator)) target = target + File.separator;
        return target;
    }

    /**
     * Remove the '/' at the beginning.
     *
     * @param target target string.
     * @return rule result.
     */
    protected String trimStartSlash(String target) {
        while (target.startsWith(File.separator)) target = target.substring(1);
        return target;
    }

    /**
     * Remove the '/' at the end of the string.
     *
     * @param target target string.
     * @return rule result.
     */
    protected String trimEndSlash(String target) {
        while (target.endsWith(File.separator)) target = target.substring(0, target.length() - 1);
        return target;
    }

    /**
     * Remove the '/' at the beginning and end of the string.
     *
     * @param target target string.
     * @return rule result.
     */
    protected String trimSlash(String target) {
        target = trimStartSlash(target);
        target = trimEndSlash(target);
        return target;
    }

    @Override
    public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
        View view = handle(request, response);
        response.setStatusCode(view.getHttpCode());
        response.setEntity(view.getHttpEntity());
        response.setHeaders(view.getHeaders());
    }

    protected View handle(HttpRequest request, HttpResponse response) throws HttpException, IOException {
        return handle(request);
    }

    protected View handle(HttpRequest request) throws HttpException, IOException {
        throw new NotFoundException(getRequestPath(request));
    }
}