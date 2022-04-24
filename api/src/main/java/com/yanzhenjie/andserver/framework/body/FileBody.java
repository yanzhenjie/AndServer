/*
 * Copyright (C) 2018 Zhenjie Yan
 *               2020 ISNing
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
import org.apache.hc.core5.http.io.entity.FileEntity;
import org.apache.hc.core5.http.nio.AsyncEntityProducer;
import org.apache.hc.core5.http.nio.entity.FileEntityProducer;

import java.io.File;

/**
 * Created by Zhenjie Yan on 2018/8/6.
 */
public class FileBody implements ResponseBody {

    private final File mBody;

    public FileBody(File body) {
        if (body == null) {
            throw new IllegalArgumentException("The file cannot be null.");
        }
        this.mBody = body;
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
        return MediaType.getFileMediaType(mBody.getName());
    }

    @NonNull
    @Override
    public AsyncEntityProducer toEntityProducer() {
        return new FileEntityProducer(mBody, ContentType.parse(getContentType()));
    }

    @NonNull
    @Override
    public HttpEntity toEntity() {
        return new FileEntity(mBody, ContentType.parse(getContentType()));
    }
}