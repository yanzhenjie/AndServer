/*
 * Copyright © 2018 YanZhenjie.
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

import android.support.annotation.NonNull;

import com.yanzhenjie.andserver.error.NotFoundException;
import com.yanzhenjie.andserver.framework.body.FileBody;
import com.yanzhenjie.andserver.http.HttpRequest;
import com.yanzhenjie.andserver.http.ResponseBody;
import com.yanzhenjie.andserver.util.Assert;
import com.yanzhenjie.andserver.util.DigestUtils;
import com.yanzhenjie.andserver.util.Patterns;
import com.yanzhenjie.andserver.util.StringUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by YanZhenjie on 2018/9/7.
 */
public class StorageWebsite extends BasicWebsite implements Patterns {

    private final String mRootPath;

    /**
     * Create a website object.
     *
     * @param rootPath website root directory.
     */
    public StorageWebsite(@NonNull String rootPath) {
        this(rootPath, DEFAULT_INDEX);
    }

    /**
     * Create a website object.
     *
     * @param rootPath website root directory.
     * @param indexFileName the default file name for each directory, e.g. index.html.
     */
    public StorageWebsite(@NonNull String rootPath, @NonNull String indexFileName) {
        super(indexFileName);
        Assert.isTrue(!StringUtils.isEmpty(rootPath), "The rootPath cannot be empty.");
        Assert.isTrue(rootPath.matches(PATH), "The format of [%s] is wrong, it should be like [/root/project].");

        this.mRootPath = rootPath;
    }

    @Override
    public boolean intercept(@NonNull HttpRequest request) {
        String httpPath = request.getPath();
        File source = findPathResource(httpPath);
        return source != null;
    }

    /**
     * Find the path specified resource.
     *
     * @param httpPath path.
     *
     * @return return if the file is found.
     */
    private File findPathResource(@NonNull String httpPath) {
        if ("/".equals(httpPath)) {
            File indexFile = new File(mRootPath, getIndexFileName());
            if (indexFile.exists() && indexFile.isFile()) {
                return indexFile;
            }
        } else {
            File sourceFile = new File(mRootPath, httpPath);
            if (sourceFile.exists()) {
                if (sourceFile.isFile()) {
                    return sourceFile;
                } else {
                    File childIndexFile = new File(sourceFile, getIndexFileName());
                    if (childIndexFile.exists() && childIndexFile.isFile()) {
                        return childIndexFile;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public String getETag(@NonNull HttpRequest request) throws IOException {
        String httpPath = request.getPath();
        File resource = findPathResource(httpPath);
        if (resource != null) {
            String tag = resource.getAbsolutePath() + resource.lastModified();
            return DigestUtils.md5DigestAsHex(tag);
        }
        return null;
    }

    @Override
    public long getLastModified(@NonNull HttpRequest request) throws IOException {
        String httpPath = request.getPath();
        File resource = findPathResource(httpPath);
        if (resource != null) return resource.lastModified();
        return -1;
    }

    @NonNull
    @Override
    public ResponseBody getBody(@NonNull HttpRequest request) throws IOException {
        String httpPath = request.getPath();
        File resource = findPathResource(httpPath);
        if (resource == null) {
            throw new NotFoundException(httpPath);
        }
        return new FileBody(resource);
    }
}