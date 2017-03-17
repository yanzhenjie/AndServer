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

import com.yanzhenjie.andserver.util.HttpRequestParser;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.FileEntity;
import org.apache.http.protocol.HttpContext;

import java.io.File;
import java.io.IOException;

/**
 * <p>Storage file handler.</p>
 * Created by Yan Zhenjie on 2017/3/15.
 */

public class StorageRequestHandler extends BasicRequestHandler {

    /**
     * Target file path.
     */
    private String mFilePath;

    /**
     * Create a handler for file.
     *
     * @param mFilePath absolute file path.
     */
    public StorageRequestHandler(String mFilePath) {
        this.mFilePath = mFilePath;
    }

    @Override
    public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
        File file = new File(mFilePath);
        if (!file.exists()) {
            requestInvalid(response);
        } else {
            response.setStatusCode(200);
            response.setEntity(new FileEntity(file, HttpRequestParser.getMimeType(file.getName())));
        }
    }

}
