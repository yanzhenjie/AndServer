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
import com.yanzhenjie.andserver.exception.NotFoundException;

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
public class StorageWebsite extends SimpleWebsite {

    private final String mRootPath;

    public StorageWebsite(String rootPath) {
        mRootPath = rootPath;
    }

    @Override
    public boolean intercept(HttpRequest request, HttpContext context) {
        String path = trimEndSlash(getRequestPath(request));
        if ("/".equals(path)) {
            File indexFile = new File(mRootPath, INDEX_FILE_PATH);
            return indexFile.exists() && indexFile.isFile();
        }
        File file = new File(mRootPath, path);
        if (file.exists()) {
            if (file.isFile()) {
                return true;
            } else {
                File childIndex = new File(file, INDEX_FILE_PATH);
                return childIndex.exists() && childIndex.isFile();
            }
        }
        return false;
    }

    @Override
    public View handle(HttpRequest request) throws HttpException, IOException {
        String httpPath = trimEndSlash(getRequestPath(request));
        if ("/".equals(httpPath)) {
            File indexFile = new File(mRootPath, INDEX_FILE_PATH);
            if (indexFile.exists() && indexFile.isFile()) {
                String mimeType = getMimeType(indexFile.getAbsolutePath());
                HttpEntity httpEntity = new FileEntity(indexFile, ContentType.create(mimeType, Charset.defaultCharset()));
                return new View(200, httpEntity);
            } else {
                throw new NotFoundException(httpPath);
            }
        }

        File targetSource = new File(mRootPath, httpPath);
        if (targetSource.exists()) {
            if (targetSource.isFile()) {
                String mimeType = getMimeType(targetSource.getAbsolutePath());
                HttpEntity httpEntity = new FileEntity(targetSource, ContentType.create(mimeType, Charset.defaultCharset()));
                return new View(200, httpEntity);
            } else {
                File childIndex = new File(targetSource, INDEX_FILE_PATH);
                if (childIndex.exists() && childIndex.isFile()) {
                    String mimeType = getMimeType(childIndex.getAbsolutePath());
                    HttpEntity httpEntity = new FileEntity(childIndex, ContentType.create(mimeType, Charset.defaultCharset()));
                    return new View(200, httpEntity);
                } else {
                    throw new NotFoundException(httpPath);
                }
            }
        } else {
            throw new NotFoundException(httpPath);
        }
    }
}