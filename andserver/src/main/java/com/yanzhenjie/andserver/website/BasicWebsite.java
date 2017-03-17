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

import android.text.TextUtils;

import java.io.File;

/**
 * <p>Basic website.</p>
 * Created by Yan Zhenjie on 2017/3/16.
 */
public abstract class BasicWebsite implements WebSite {

    /**
     * Default index page.
     */
    protected final String INDEX_HTML;

    /**
     * Basic Website.
     *
     * @param rootPath site root directory.
     */
    public BasicWebsite(String rootPath) {
        this.INDEX_HTML = TextUtils.isEmpty(rootPath) ? "index.html" : (rootPath + File.separator + "index.html");
    }

    /**
     * Remove the '/' at the beginning and end of the string.
     *
     * @param target target string.
     * @return rule result.
     */
    public static String trimSlash(String target) {
        while (target.startsWith(File.separator)) target = target.substring(1);
        while (target.endsWith(File.separator)) target = target.substring(0, target.length() - 1);
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

}
