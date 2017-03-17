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
package com.yanzhenjie.andserver.sample.response;

import com.yanzhenjie.andserver.RequestHandler;
import com.yanzhenjie.andserver.util.HttpRequestParser;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;

import java.io.File;
import java.io.IOException;

/**
 * <p>Returns a file.</p>
 * Created by Yan Zhenjie on 2016/7/1.
 */
public class RequestFileHandler implements RequestHandler {

    private String mFilePath;

    public RequestFileHandler(String filePath) {
        this.mFilePath = filePath;
    }

    @Override
    public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
        // You can according to the client param can also be downloaded.

        File file = new File(mFilePath);
        if (file.exists()) {
            response.setStatusCode(200);

            long contentLength = file.length();
            response.setHeader("ContentLength", Long.toString(contentLength));
            response.setEntity(new FileEntity(file, HttpRequestParser.getMimeType(file.getName())));
        } else {
            response.setStatusCode(404);
            response.setEntity(new StringEntity(""));
        }
    }


}
