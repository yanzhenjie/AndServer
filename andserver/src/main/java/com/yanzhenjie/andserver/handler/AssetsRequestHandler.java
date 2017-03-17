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
package com.yanzhenjie.andserver.handler;

import com.yanzhenjie.andserver.util.AssetsWrapper;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.io.InputStream;

/**
 * <p>Asset file handler.</p>
 * Created by Yan Zhenjie on 2017/3/15.
 */

public class AssetsRequestHandler extends BasicRequestHandler {

    /**
     * Asset handler wrapper.
     */
    private AssetsWrapper mAssetsWrapper;

    /**
     * Target file path.
     */
    private String mFilePath;

    /**
     * Create a handler for file.
     *
     * @param assetsWrapper Asset handler wrapper.
     * @param mFilePath     absolute file path.
     */
    public AssetsRequestHandler(AssetsWrapper assetsWrapper, String mFilePath) {
        this.mAssetsWrapper = assetsWrapper;
        this.mFilePath = mFilePath;
    }

    @Override
    public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
        InputStream stream = mAssetsWrapper.getInputStream(mFilePath);
        if (stream == null) {
            requestInvalid(response);
        } else {
            response.setStatusCode(200);
            response.setEntity(new InputStreamEntity(stream, stream.available()));
        }
    }

}
