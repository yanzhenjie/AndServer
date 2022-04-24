/*
 * Copyright (C) 2018 Zhenjie Yan
 *               2022 ISNing
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
import com.yanzhenjie.andserver.util.MediaType;

import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.InputStreamEntity;
import org.apache.hc.core5.http.nio.AsyncEntityProducer;
import org.apache.hc.core5.http.nio.DataStreamChannel;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Set;

/**
 * Created by Zhenjie Yan on 2018/9/7.
 */
public class StreamBody implements ResponseBody {

    private static final int BUFFER = 8 * 1024;

    HttpEntity entity;

    private final InputStream mStream;
    private long mLength;
    private final MediaType mMediaType;

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

    @NonNull
    @Override
    public AsyncEntityProducer toEntityProducer() {
        return new AsyncEntityProducer() {
            @Override
            public boolean isRepeatable() {
                return false;
            }

            @Override
            public void failed(Exception cause) {

            }

            @Override
            public long getContentLength() {
                return StreamBody.this.getContentLength();
            }

            @Override
            public String getContentType() {
                return StreamBody.this.getContentType();
            }

            @Override
            public String getContentEncoding() {
                return null;
            }

            @Override
            public boolean isChunked() {
                return false;
            }

            @Override
            public Set<String> getTrailerNames() {
                return null;
            }

            @Override
            public int available() {
                try {
                    return mStream.available();
                } catch (IOException e) {
                    e.printStackTrace(System.err);
                    return 0;
                }
            }

            @Override
            public void produce(DataStreamChannel channel) throws IOException {
                byte[] buf = new byte[BUFFER];
                while (mStream.read(buf) != -1) {
                    channel.write(ByteBuffer.wrap(buf));
                }
                channel.endStream();
                releaseResources();
            }

            @Override
            public void releaseResources() {
                try {
                    mStream.close();
                } catch (IOException e) {
                    e.printStackTrace(System.err);
                }
            }
        };
    }

    @NonNull
    @Override
    public HttpEntity toEntity() {
        return new InputStreamEntity(mStream, this.getContentLength(), ContentType.parse(getContentType()));
    }

    public long getContentLength() {
        if (mLength == 0 && mStream instanceof FileInputStream) {
            try {
                mLength = ((FileInputStream) mStream).getChannel().size();
                return mLength;
            } catch (IOException e) {
                e.printStackTrace(System.err);
            }
        }
        return mLength;
    }

    public String getContentType() {
        MediaType mimeType = this.getContentTypeMedia();
        if (mimeType == null) {
            return null;
        }
        return mimeType.toString();
    }

    @Nullable
    @Override
    public MediaType getContentTypeMedia() {
        return mMediaType;
    }
}