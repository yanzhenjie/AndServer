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

import android.content.res.AssetManager;

import com.yanzhenjie.andserver.RequestHandler;
import com.yanzhenjie.andserver.handler.AssetsRequestHandler;
import com.yanzhenjie.andserver.util.AssetsWrapper;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * <p>The web site in assets.</p>
 * Created by Yan Zhenjie on 2017/3/15.
 */

public class AssetsWebsite extends BasicWebsite {

    /**
     * AssetsWrapper.
     */
    private AssetsWrapper mAssetsWrapper;

    /**
     * Site root directory.
     */
    private String mRootPath;

    /**
     * Assets Website.
     *
     * @param assetManager {@link AssetsWrapper}.
     * @param rootPath     site root directory in assets, such as: {@code ""}, {@code "website"}.
     */
    public AssetsWebsite(AssetManager assetManager, String rootPath) {
        super(rootPath);
        if (rootPath == null) rootPath = "";
        this.mRootPath = trimSlash(rootPath);
        this.mAssetsWrapper = new AssetsWrapper(assetManager);
    }

    @Override
    public void onRegister(Map<String, RequestHandler> handlerMap) {
        RequestHandler indexHandler = new AssetsRequestHandler(mAssetsWrapper, INDEX_HTML);
        handlerMap.put("", indexHandler);
        handlerMap.put(mRootPath, indexHandler);
        handlerMap.put(mRootPath + File.separator, indexHandler);
        handlerMap.put(mRootPath + File.separator + INDEX_HTML, indexHandler);

        List<String> pathList = mAssetsWrapper.scanFile(mRootPath);
        for (String path : pathList) {
            handlerMap.put(path, new AssetsRequestHandler(mAssetsWrapper, path));
        }
    }
}
