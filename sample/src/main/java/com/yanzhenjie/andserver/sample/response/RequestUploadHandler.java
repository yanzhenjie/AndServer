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

import android.os.Environment;

import com.yanzhenjie.andserver.RequestHandler;
import com.yanzhenjie.andserver.upload.HttpFileUpload;
import com.yanzhenjie.andserver.upload.HttpUploadContext;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * <p>Upload file handler.</p>
 * Created by Yan Zhenjie on 2016/6/13.
 */
public class RequestUploadHandler implements RequestHandler {

    @Override
    public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
        // HttpFileUpload.isMultipartContent(request) // DELETE、PUT、POST method。

        if (!HttpFileUpload.isMultipartContentWithPost(request)) { // Is POST and upload.
            response(403, "You must upload file.", response);
        } else {
            // File save directory.
            final File saveDirectory = Environment.getExternalStorageDirectory();

            if (saveDirectory.isDirectory()) {
                try {
                    processFileUpload(request, saveDirectory);
                    response(200, "Ok.", response);
                } catch (Exception e) {
                    e.printStackTrace();
                    response(500, "Save the file when the error occurs.", response);
                }
            } else {
                response(500, "The server can not save the file.", response);
            }
        }
    }

    private void response(int responseCode, String message, HttpResponse response) throws IOException {
        response.setStatusCode(responseCode);
        response.setEntity(new StringEntity(message, "utf-8"));
    }

    /**
     * Parse file and save.
     *
     * @param request       request.
     * @param saveDirectory save directory.
     * @throws Exception may be.
     */
    private void processFileUpload(HttpRequest request, File saveDirectory) throws Exception {
        FileItemFactory factory = new DiskFileItemFactory(1024 * 1024, saveDirectory);
        HttpFileUpload fileUpload = new HttpFileUpload(factory);

        // Set upload process listener.
        // fileUpload.setProgressListener(new ProgressListener(){...});

        List<FileItem> fileItems = fileUpload.parseRequest(new HttpUploadContext((HttpEntityEnclosingRequest) request));

        for (FileItem fileItem : fileItems) {
            if (!fileItem.isFormField()) { // File param.
                // Attribute.
                // fileItem.getContentType();
                // fileItem.getFieldName();
                // fileItem.getName();
                // fileItem.getSize();
                // fileItem.getString();

                File uploadedFile = new File(saveDirectory, fileItem.getName());
                // 把流写到文件上。
                fileItem.write(uploadedFile);
            } else { // General param.
                String key = fileItem.getName();
                String value = fileItem.getString();
            }
        }
    }
}
