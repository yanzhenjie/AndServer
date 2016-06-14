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

import com.yanzhenjie.andserver.AndServerRequestHandler;
import com.yanzhenjie.andserver.util.HttpRequestParser;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.ProgressListener;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.httpserv.HttpServFileUpload;
import org.apache.commons.fileupload.httpserv.HttpServRequestContext;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * <p>上传文件的接口。</p>
 * Created on 2016/6/13.
 *
 * @author Yan Zhenjie.
 */
public class AndServerUploadHandler implements AndServerRequestHandler {

    @Override
    public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
        if (HttpServFileUpload.isMultipartContent(request)) {//判断是否是带文件上传的表单

            // 这里可以先拿到参数，传文件的时候需要传的话。
            Map<String, String> params = HttpRequestParser.parse(request);//拿到参数

            // 文件保存目录
            final File saveUpLoadDir = Environment.getExternalStorageDirectory();

            if (saveUpLoadDir.isDirectory()) {
                try {
                    // 保存文件。
                    processFileUpload(request, saveUpLoadDir);
                    response(200, "Ok.", response);
                } catch (Exception e) {
                    e.printStackTrace();
                    response(500, "Save the file when the error occurs.", response);
                }
            } else {
                response(400, "The server can not save the file.", response);
            }
        } else {// 如果不是上传文件，告诉客户端禁止访问。
            response(403, "You must upload file, contentType is multipart/form-data.", response);
        }
    }

    /**
     * 解析文件并保存到SD卡。
     *
     * @param request   {@link HttpRequest}.
     * @param uploadDir 文件保存文件夹。
     * @throws Exception 保存文件时可能发生。
     */
    private void processFileUpload(HttpRequest request, File uploadDir) throws Exception {
        FileItemFactory factory = new DiskFileItemFactory(1024 * 1024, uploadDir);
        HttpServFileUpload fileUpload = new HttpServFileUpload(factory);

        // 设置上传进度监听，可以在这个类中，发广播或者handler更新UI。
        fileUpload.setProgressListener(new AndWebProgressListener());

        List<FileItem> fileItems = fileUpload.parseRequest(new HttpServRequestContext(request));

        for (FileItem fileItem : fileItems) {
            if (!fileItem.isFormField()) {
                // 这里有很多属性可以拿到。
//                fileItem.getContentType()
                File uploadedFile = new File(uploadDir, fileItem.getName());

                // 把流写到文件上。
                fileItem.write(uploadedFile);

            }
        }
    }

    /**
     * 发送响应消息。
     *
     * @param responseCode 响应码。
     * @param message      响应消息。
     * @param response     响应。
     * @throws Exception 发送数据的时候可能异常。
     */
    private void response(int responseCode, String message, HttpResponse response) throws IOException {
        response.setStatusCode(responseCode);
        response.setEntity(new StringEntity(message, "utf-8"));
    }

    private class AndWebProgressListener implements ProgressListener {
        /**
         * 更新进度.
         *
         * @param pBytesRead     读到现在为止的字节总数。
         * @param pContentLength 内容总大小，如果大小是未知的，那么这个数字是-1。
         * @param pItems         字段的下标，哪一个正在被读。如果是0，没有字段被读取，如果是1，代表第一个正在被读。
         */
        @Override
        public void update(long pBytesRead, long pContentLength, int pItems) {
            if (pContentLength != -1) {
                int progress = (int) (pBytesRead * 100 / pContentLength);

            }
        }
    }
}
