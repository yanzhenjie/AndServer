/*
 * Copyright © 2018 Zhenjie Yan.
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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Zhenjie Yan on 2018/9/7.
 */
public class StreamBody implements ResponseBody {

    private InputStream mStream;
    private long mLength;
    private MediaType mMediaType;

    public StreamBody(InputStream stream) {
        this(stream, MediaType.APPLICATION_OCTET_STREAM);
    }

    public StreamBody(InputStream stream, long length) {
        this(stream, length, MediaType.APPLICATION_OCTET_STREAM);
    }

    public StreamBody(InputStream stream, MediaType mediaType) {
        this(stream, 0, mediaType);
    }

    public StreamBody(InputStream stream, long length, MediaType mediaType) {
        this.mStream = stream;
        this.mLength = length;
        this.mMediaType = mediaType;
    }

    @Override
    public boolean isRepeatable() {
        return false;
    }

    @Override
    public long contentLength() {
        if (mLength == 0 && mStream instanceof FileInputStream) {
            try {
                mLength = ((FileInputStream) mStream).getChannel().size();
                return mLength;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return mLength;
    }

    @Nullable
    @Override
    public MediaType contentType() {
        return mMediaType;
    }

    @Override
    public void writeTo(@NonNull OutputStream output) throws IOException {
        IOUtils.write(mStream, output);
        IOUtils.closeQuietly(mStream);
    }
}