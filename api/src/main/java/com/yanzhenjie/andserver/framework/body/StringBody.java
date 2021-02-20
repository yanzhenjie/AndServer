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
package com.yanzhenjie.andserver.framework.body;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yanzhenjie.andserver.http.ResponseBody;
import com.yanzhenjie.andserver.util.IOUtils;
import com.yanzhenjie.andserver.util.MediaType;

import org.apache.commons.io.Charsets;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

/**
 * Created by Zhenjie Yan on 2018/8/6.
 */
public class StringBody implements ResponseBody {

    private byte[] mBody;
    private MediaType mMediaType;

    public StringBody(String body) {
        this(body, MediaType.TEXT_PLAIN);
    }

    public StringBody(String body, MediaType mediaType) {
        if (body == null) {
            throw new IllegalArgumentException("The content cannot be null.");
        }

        this.mMediaType = mediaType;
        if (mMediaType == null) {
            mMediaType = new MediaType(MediaType.TEXT_PLAIN, Charsets.toCharset("utf-8"));
        }

        Charset charset = mMediaType.getCharset();
        if (charset == null) {
            charset = Charsets.toCharset("utf-8");
        }
        this.mBody = body.getBytes(charset);
    }

    @Override
    public boolean isRepeatable() {
        return true;
    }

    @Override
    public long contentLength() {
        return mBody.length;
    }

    @Nullable
    @Override
    public MediaType contentType() {
        Charset charset = mMediaType.getCharset();
        if (charset == null) {
            charset = Charsets.toCharset("utf-8");
            return new MediaType(mMediaType.getType(), mMediaType.getSubtype(), charset);
        }
        return mMediaType;
    }

    @Override
    public void writeTo(@NonNull OutputStream output) throws IOException {
        IOUtils.write(output, mBody);
    }
}