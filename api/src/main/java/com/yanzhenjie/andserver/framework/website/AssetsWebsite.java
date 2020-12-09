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

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yanzhenjie.andserver.error.NotFoundException;
import com.yanzhenjie.andserver.framework.body.StreamBody;
import com.yanzhenjie.andserver.framework.body.StringBody;
import com.yanzhenjie.andserver.http.HttpRequest;
import com.yanzhenjie.andserver.http.HttpResponse;
import com.yanzhenjie.andserver.http.ResponseBody;
import com.yanzhenjie.andserver.util.Assert;
import com.yanzhenjie.andserver.util.DigestUtils;
import com.yanzhenjie.andserver.util.IOUtils;
import com.yanzhenjie.andserver.util.MediaType;
import com.yanzhenjie.andserver.util.Patterns;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Zhenjie Yan on 2018/9/7.
 */
public class AssetsWebsite extends BasicWebsite implements Patterns {

    private final AssetsReader mAssetsReader;
    private final String mRootPath;
    private final PackageInfo mPackageInfo;

    /**
     * Create a website object.
     *
     * @param rootPath website root directory.
     */
    public AssetsWebsite(@NonNull Context context, @NonNull String rootPath) {
        this(context, rootPath, DEFAULT_INDEX);
    }

    /**
     * Create a website object.
     *
     * @param rootPath website root directory.
     * @param indexFileName the default file name for each directory, e.g. index.html.
     */
    public AssetsWebsite(@NonNull Context context, @NonNull String rootPath, @NonNull String indexFileName) {
        super(indexFileName);
        Assert.isTrue(!TextUtils.isEmpty(rootPath), "The rootPath cannot be empty.");
        Assert.isTrue(!TextUtils.isEmpty(indexFileName), "The indexFileName cannot be empty.");

        if (!rootPath.matches(PATH)) {
            String message = "The format of [%s] is wrong, it should be like [/root/project] or [/root/project/].";
            String format = String.format(message, rootPath);
            throw new IllegalArgumentException(format);
        }

        this.mAssetsReader = new AssetsReader(context.getAssets());
        this.mRootPath = trimSlash(rootPath);

        PackageManager packageManager = context.getPackageManager();
        try {
            mPackageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean intercept(@NonNull HttpRequest request) {
        String httpPath = request.getPath();
        InputStream stream = findPathSteam(httpPath);
        IOUtils.closeQuietly(stream);
        return stream != null;
    }

    @Override
    public String getETag(@NonNull HttpRequest request) throws Throwable {
        String httpPath = request.getPath();
        InputStream stream = findPathSteam(httpPath);
        if (stream != null) {
            try {
                return DigestUtils.md5DigestAsHex(stream);
            } finally {
                IOUtils.closeQuietly(stream);
            }
        }
        return null;
    }

    @Override
    public long getLastModified(@NonNull HttpRequest request) throws Throwable {
        String httpPath = request.getPath();
        InputStream stream = findPathSteam(httpPath);
        IOUtils.closeQuietly(stream);
        return stream != null ? mPackageInfo.lastUpdateTime : -1;
    }

    @NonNull
    @Override
    public ResponseBody getBody(@NonNull HttpRequest request, @NonNull HttpResponse response) throws IOException {
        String httpPath = request.getPath();
        String objectPath = mRootPath + httpPath;
        InputStream stream = mAssetsReader.getInputStream(objectPath);
        if (stream != null) {
            MediaType mediaType = MediaType.getFileMediaType(objectPath);
            return new StreamBody(stream, stream.available(), mediaType);
        }

        String indexPath = addEndSlash(objectPath) + getIndexFileName();
        InputStream indexStream = mAssetsReader.getInputStream(indexPath);
        if (indexStream != null) {
            if (!httpPath.endsWith(File.separator)) {
                IOUtils.closeQuietly(indexStream);
                String redirectPath = addEndSlash(httpPath);
                String query = queryString(request);
                response.sendRedirect(redirectPath + "?" + query);
                return new StringBody("");
            }

            final MediaType mediaType = MediaType.getFileMediaType(indexPath);
            return new StreamBody(indexStream, indexStream.available(), mediaType);
        }

        throw new NotFoundException(httpPath);
    }

    private InputStream findPathSteam(String httpPath) {
        String targetPath = mRootPath + httpPath;
        InputStream targetStream = mAssetsReader.getInputStream(targetPath);
        if (targetStream != null) {
            return targetStream;
        }

        String indexPath = addEndSlash(targetPath) + getIndexFileName();
        InputStream indexStream = mAssetsReader.getInputStream(indexPath);
        if (indexStream != null) {
            return indexStream;
        }

        return null;
    }

    public static class AssetsReader {

        /**
         * {@link AssetManager}.
         */
        private AssetManager mAssetManager;

        /**
         * Create {@link AssetsReader}.
         *
         * @param manager {@link AssetManager}.
         */
        public AssetsReader(@NonNull AssetManager manager) {
            this.mAssetManager = manager;
        }

        /**
         * Get stream file.
         *
         * @param filePath assets in the absolute path.
         *
         * @return {@link InputStream} or null.
         */
        @Nullable
        public InputStream getInputStream(@NonNull String filePath) {
            try {
                return mAssetManager.open(filePath);
            } catch (Throwable ignored) {
                return null;
            }
        }

        /**
         * Specify whether the destination is a file.
         *
         * @param fileName assets in the absolute path.
         *
         * @return true, other wise is false.
         */
        public boolean isFile(@NonNull String fileName) {
            InputStream stream = null;
            try {
                stream = getInputStream(fileName);
                return stream != null;
            } finally {
                IOUtils.closeQuietly(stream);
            }
        }

        /**
         * Scanning subFolders and files under the specified path.
         *
         * @param path the specified path.
         *
         * @return String[] Array of strings, one for each asset. May be null.
         */
        @NonNull
        public List<String> list(@NonNull String path) {
            List<String> fileList = new ArrayList<>();
            try {
                String[] files = mAssetManager.list(path);
                Collections.addAll(fileList, files);
            } catch (Throwable ignored) {
            }
            return fileList;
        }

        /**
         * Scan all files in the inPath.
         *
         * @param path path in the path.
         *
         * @return under inPath absolute path.
         */
        @NonNull
        public List<String> scanFile(@NonNull String path) {
            Assert.isTrue(!TextUtils.isEmpty(path), "The path cannot be empty.");

            List<String> pathList = new ArrayList<>();
            if (isFile(path)) {
                pathList.add(path);
            } else {
                List<String> files = list(path);
                for (String file: files) {
                    String realPath = path + File.separator + file;
                    if (isFile(realPath)) {
                        pathList.add(realPath);
                    } else {
                        List<String> childList = scanFile(realPath);
                        if (childList.size() > 0) {
                            pathList.addAll(childList);
                        }
                    }
                }
            }
            return pathList;
        }
    }
}