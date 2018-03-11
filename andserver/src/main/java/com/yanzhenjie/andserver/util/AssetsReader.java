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
package com.yanzhenjie.andserver.util;

import android.content.res.AssetManager;
import android.text.TextUtils;

import java.io.File;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>
 * AssetManager wrapper.
 * </p>
 * Created by Yan Zhenjie on 2017/3/15.
 */
public class AssetsReader {

    /**
     * {@link AssetManager}.
     */
    private AssetManager mAssetManager;

    /**
     * Create {@link AssetsReader}.
     *
     * @param assetManager {@link AssetManager}, such as: context.getAssets();
     */
    public AssetsReader(AssetManager assetManager) {
        this.mAssetManager = assetManager;
    }

    /**
     * Get stream file.
     *
     * @param fileName assets in the absolute path.
     * @return {@link InputStream} or null.
     */
    public InputStream getInputStream(String fileName) {
        try {
            return mAssetManager.open(fileName);
        } catch (Throwable ignored) {
            return null;
        }
    }

    /**
     * Specify whether the destination is a file.
     *
     * @param fileName assets in the absolute path.
     * @return true, other wise is false.
     */
    public boolean isFile(String fileName) {
        return getInputStream(fileName) != null;
    }

    /**
     * Scanning subFolders and files under the specified path.
     *
     * @param path the specified path.
     * @return String[] Array of strings, one for each asset. May be null.
     */
    public String[] fileList(String path) {
        try {
            return mAssetManager.list(path);
        } catch (Throwable ignored) {
            return null;
        }
    }

    /**
     * Scan all files in the inPath.
     *
     * @param inPath path in the path.
     * @return under inPath absolute path.
     */
    public List<String> scanFile(String inPath) {
        List<String> pathList = new LinkedList<>();
        if (isFile(inPath)) {
            pathList.add(inPath);
        } else {
            String[] files = fileList(inPath);
            if (files != null && files.length > 0) {
                for (String file : files) {
                    String realPath = (TextUtils.isEmpty(inPath) ? "" : (inPath + File.separator)) + file;
                    if (isFile(realPath)) pathList.add(realPath);
                    else {
                        List<String> childList = scanFile(realPath);
                        if (childList.size() > 0) pathList.addAll(childList);
                    }
                }
            }
        }
        return pathList;
    }

}
