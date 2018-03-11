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

import com.yanzhenjie.andserver.exception.NotFoundException;
import com.yanzhenjie.andserver.protocol.ETag;
import com.yanzhenjie.andserver.protocol.LastModified;
import com.yanzhenjie.andserver.view.View;

import org.apache.httpcore.HttpEntity;
import org.apache.httpcore.HttpException;
import org.apache.httpcore.HttpRequest;
import org.apache.httpcore.entity.ContentType;
import org.apache.httpcore.entity.FileEntity;
import org.apache.httpcore.protocol.HttpContext;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import static com.yanzhenjie.andserver.util.FileUtils.getMimeType;
import static com.yanzhenjie.andserver.util.HttpRequestParser.getRequestPath;

/**
 * <p>
 * The web site in storage.
 * </p>
 * Created by Yan Zhenjie on 2017/3/15.
 */
public class StorageWebsite extends SimpleWebsite implements LastModified, ETag {

    private final String mRootPath;

    public StorageWebsite(String rootPath) {
        this.mRootPath = rootPath;
    }

    @Override
    public boolean intercept(HttpRequest request, HttpContext context) throws HttpException, IOException {
        String httpPath = getRequestPath(request);
        httpPath = "/".equals(httpPath) ? "/" : trimEndSlash(getRequestPath(request));
        File source = findPathSource(httpPath);
        return source != null;
    }

    /**
     * Find the path specified resource.
     *
     * @param httpPath path.
     * @return return if the file is found.
     */
    private File findPathSource(String httpPath) {
        if ("/".equals(httpPath)) {
            File indexFile = new File(mRootPath, INDEX_FILE_PATH);
            if (indexFile.exists() && indexFile.isFile()) {
                return indexFile;
            }
        } else {
            File sourceFile = new File(mRootPath, httpPath);
            if (sourceFile.exists()) {
                if (sourceFile.isFile()) {
                    return sourceFile;
                } else {
                    File childIndexFile = new File(sourceFile, INDEX_FILE_PATH);
                    if (childIndexFile.exists() && childIndexFile.isFile()) {
                        return childIndexFile;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public View handle(HttpRequest request) throws HttpException, IOException {
        String httpPath = trimEndSlash(getRequestPath(request));
        File source = findPathSource(httpPath);
        if (source == null)
            throw new NotFoundException(httpPath);
        return generateSourceView(source);
    }

    /**
     * Generate {@code View} for source.
     *
     * @param source file.
     * @return view of source.
     */
    private View generateSourceView(File source) throws IOException {
        String mimeType = getMimeType(source.getAbsolutePath());
        HttpEntity httpEntity = new FileEntity(source, ContentType.create(mimeType, Charset.defaultCharset()));
        return new View(200, httpEntity);
    }

    @Override
    public long getLastModified(HttpRequest request) throws IOException {
        String httpPath = trimEndSlash(getRequestPath(request));
        File source = findPathSource(httpPath);
        if (source != null)
            return source.lastModified();
        return -1;
    }

    @Override
    public String getETag(HttpRequest request) throws IOException {
        String httpPath = trimEndSlash(getRequestPath(request));
        File source = findPathSource(httpPath);
        if (source != null) {
            long sourceSize = source.length();
            String sourcePath = source.getAbsolutePath();
            long lastModified = source.lastModified();
            return sourceSize + sourcePath + lastModified;
        }
        return null;
    }
}