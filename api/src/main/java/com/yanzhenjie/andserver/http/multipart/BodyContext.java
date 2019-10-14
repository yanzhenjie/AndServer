/*
 * Copyright 2018 Zhenjie Yan.
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
package com.yanzhenjie.andserver.http.multipart;

import androidx.annotation.NonNull;

import com.yanzhenjie.andserver.http.RequestBody;
import com.yanzhenjie.andserver.util.MediaType;

import org.apache.commons.fileupload.UploadContext;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Zhenjie Yan on 2018/8/9.
 */
public class BodyContext implements UploadContext {

    private final RequestBody mBody;

    public BodyContext(@NonNull RequestBody body) {
        this.mBody = body;
    }

    @Override
    public String getCharacterEncoding() {
        return mBody.contentEncoding();
    }

    @Override
    public String getContentType() {
        MediaType contentType = mBody.contentType();
        return contentType == null ? null : contentType.toString();
    }

    @Override
    public int getContentLength() {
        long contentLength = contentLength();
        return contentLength > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) contentLength;
    }

    @Override
    public long contentLength() {
        return mBody.length();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return mBody.stream();
    }

    @Override
    public String toString() {
        return String.format("ContentLength=%s, Mime=%s", contentLength(), getContentType());
    }
}