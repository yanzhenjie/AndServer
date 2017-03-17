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

import com.yanzhenjie.andserver.RequestHandler;
import com.yanzhenjie.andserver.handler.StorageRequestHandler;
import com.yanzhenjie.andserver.util.StorageWrapper;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * <p>The web site in storage.</p>
 * Created by Yan Zhenjie on 2017/3/15.
 */

public class StorageWebsite extends BasicWebsite {

    /**
     * StorageWrapper.
     */
    private StorageWrapper mStorageWrapper;

    /**
     * Site root directory.
     */
    private String mRootPath;

    /**
     * Storage Website.
     *
     * @param rootPath site root directory in assets, such as: {@code "/storage/sdcard/google/website"}.
     */
    public StorageWebsite(String rootPath) {
        super(rootPath);
        if (TextUtils.isEmpty(rootPath)) throw new NullPointerException("The RootPath can not be null.");
        this.mRootPath = trimSlash(rootPath);
        mStorageWrapper = new StorageWrapper();
    }

    @Override
    public void onRegister(Map<String, RequestHandler> handlerMap) {
        RequestHandler indexHandler = new StorageRequestHandler(INDEX_HTML);
        handlerMap.put("", indexHandler);
        handlerMap.put(mRootPath, indexHandler);
        handlerMap.put(mRootPath + File.separator, indexHandler);
        handlerMap.put(mRootPath + File.separator + INDEX_HTML, indexHandler);

        List<String> pathList = mStorageWrapper.scanFile(getHttpPath(mRootPath));
        for (String path : pathList) {
            RequestHandler requestHandler = new StorageRequestHandler(path);
            handlerMap.put(path, requestHandler);
        }
    }

}
