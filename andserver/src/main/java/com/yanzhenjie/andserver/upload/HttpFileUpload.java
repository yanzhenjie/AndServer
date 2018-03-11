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
package com.yanzhenjie.andserver.upload;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.httpcore.HttpEntityEnclosingRequest;
import org.apache.httpcore.HttpRequest;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by Yan Zhenjie on 2017/3/16.
 */
public class HttpFileUpload extends FileUpload {
    
    public HttpFileUpload() {
    }

    public HttpFileUpload(FileItemFactory fileItemFactory) {
        super(fileItemFactory);
    }

    public List<FileItem> parseRequest(HttpRequest request) throws FileUploadException {
        return parseRequest(new HttpUploadContext((HttpEntityEnclosingRequest) request));
    }

    public Map<String, List<FileItem>> parseParameterMap(HttpRequest request) throws FileUploadException {
        return parseParameterMap(new HttpUploadContext((HttpEntityEnclosingRequest) request));
    }

    public FileItemIterator getItemIterator(HttpRequest request) throws FileUploadException, IOException {
        return getItemIterator(new HttpUploadContext((HttpEntityEnclosingRequest) request));
    }
}
