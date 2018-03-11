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

import org.apache.commons.fileupload.UploadContext;
import org.apache.httpcore.Header;
import org.apache.httpcore.HttpEntity;
import org.apache.httpcore.HttpEntityEnclosingRequest;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Yan Zhenjie on 2017/3/16.
 */
public class HttpUploadContext implements UploadContext {

    private final HttpEntity mEntity;

    public HttpUploadContext(HttpEntityEnclosingRequest request) {
        this.mEntity = request.getEntity();
    }

    @Override
    public String getCharacterEncoding() {
        Header header = mEntity.getContentEncoding();
        return header == null ? null : header.getValue();
    }

    @Override
    public String getContentType() {
        Header header = mEntity.getContentType();
        return header == null ? null : header.getValue();
    }

    @Override
    public int getContentLength() {
        long contentLength = contentLength();
        return contentLength > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) contentLength;
    }

    @Override
    public long contentLength() {
        return mEntity.getContentLength();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return this.mEntity.getContent();
    }
}
