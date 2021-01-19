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
import androidx.annotation.Nullable;

import com.yanzhenjie.andserver.error.NotFoundException;
import com.yanzhenjie.andserver.framework.body.FileBody;
import com.yanzhenjie.andserver.framework.body.StringBody;
import com.yanzhenjie.andserver.http.HttpRequest;
import com.yanzhenjie.andserver.http.HttpResponse;
import com.yanzhenjie.andserver.http.ResponseBody;
import com.yanzhenjie.andserver.util.Assert;
import com.yanzhenjie.andserver.util.DigestUtils;
import com.yanzhenjie.andserver.util.IOUtils;
import com.yanzhenjie.andserver.util.MediaType;
import com.yanzhenjie.andserver.util.Patterns;

import org.apache.commons.io.Charsets;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Zhenjie Yan on 2018/9/7.
 */
public class FileBrowser extends BasicWebsite implements Patterns {

    private static final String FOLDER_HTML_PREFIX = "<!DOCTYPE html><html><head><meta http-equiv=\"content-type\" " +
        "content=\"text/html; charset=utf-8\"/> <meta name=\"viewport\" content=\"width=device-width, " +
        "initial-scale=1, user-scalable=no\"><metaname=\"format-detection\" content=\"telephone=no\"/> " +
        "<title>%1$s</title><style>.center_horizontal{margin:0 auto;text-align:center;} *,*::after,*::before " +
        "{box-sizing: border-box;margin: 0;padding: 0;}a:-webkit-any-link {color: -webkit-link;cursor: auto;" +
        "text-decoration: underline;}ul {list-style: none;display: block;list-style-type: none;-webkit-margin-before:" +
        " 1em;-webkit-margin-after: 1em;-webkit-margin-start: 0px;-webkit-margin-end: 0px;-webkit-padding-start: " +
        "40px;}li {display: list-item;text-align: -webkit-match-parent;margin-bottom: 5px;}</style></head><body><h1 " +
        "class=\"center_horizontal\">%2$s</h1><ul>";
    private static final String FOLDER_ITEM = "<li><a href=\"%1$s\">%2$s</a></li>";
    private static final String FOLDER_HTML_SUFFIX = "</ul></body></html>";

    private final String mRootPath;

    public FileBrowser(String rootPath) {
        Assert.isTrue(!TextUtils.isEmpty(rootPath), "The rootPath cannot be empty.");
        Assert.isTrue(rootPath.matches(PATH), "The format of [%s] is wrong, it should be like [/root/project].");
        this.mRootPath = rootPath;
    }

    @Override
    public boolean intercept(@NonNull HttpRequest request) {
        String httpPath = request.getPath();
        File file = findPathFile(httpPath);
        return file != null;
    }

    @Override
    public String getETag(@NonNull HttpRequest request) throws Throwable {
        String httpPath = request.getPath();
        File file = findPathFile(httpPath);
        if (file != null) {
            String tag = file.getAbsolutePath() + file.lastModified();
            return DigestUtils.md5DigestAsHex(tag);
        }
        return null;
    }

    @Override
    public long getLastModified(@NonNull HttpRequest request) throws Throwable {
        String httpPath = request.getPath();
        File file = findPathFile(httpPath);
        if (file != null) {
            return file.lastModified();
        }
        return -1;
    }

    @NonNull
    @Override
    public ResponseBody getBody(@NonNull HttpRequest request, @NonNull HttpResponse response) throws IOException {
        String httpPath = request.getPath();
        File file = new File(mRootPath, httpPath);
        if (!file.exists()) {
            throw new NotFoundException(httpPath);
        }

        if (file.isDirectory()) {
            if (!httpPath.endsWith(File.separator)) {
                String redirectPath = addEndSlash(httpPath);
                response.sendRedirect(redirectPath);
                return new StringBody("");
            }

            File tempFile = File.createTempFile("file_browser", ".html");
            OutputStream outputStream = new FileOutputStream(tempFile);

            String folderName = file.getName();
            String prefix = String.format(FOLDER_HTML_PREFIX, folderName, folderName);
            outputStream.write(prefix.getBytes("utf-8"));

            File[] children = file.listFiles();
            if (children != null && children.length > 0) {
                for (File child: children) {
                    String filePath = child.getAbsolutePath();
                    int rootIndex = filePath.indexOf(mRootPath);
                    String subHttpPath = filePath.substring(rootIndex + mRootPath.length());
                    subHttpPath = addStartSlash(subHttpPath);
                    String fileItem = String.format(FOLDER_ITEM, subHttpPath, child.getName());
                    outputStream.write(fileItem.getBytes("utf-8"));
                }
            }

            outputStream.write(FOLDER_HTML_SUFFIX.getBytes("utf-8"));
            IOUtils.closeQuietly(outputStream);

            return new FileBody(tempFile) {
                @Nullable
                @Override
                public MediaType contentType() {
                    MediaType mimeType = super.contentType();
                    if (mimeType != null) {
                        mimeType = new MediaType(mimeType.getType(),
                            mimeType.getSubtype(),
                            Charsets.toCharset("utf-8"));
                    }
                    return mimeType;
                }
            };
        } else {
            return new FileBody(file);
        }
    }

    /**
     * Find the path specified resource.
     *
     * @param httpPath path.
     *
     * @return return if the file is found.
     */
    private File findPathFile(@NonNull String httpPath) {
        File targetFile = new File(mRootPath, httpPath);
        if (targetFile.exists()) {
            return targetFile;
        }
        return null;
    }
}