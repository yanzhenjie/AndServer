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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Zhenjie Yan on 2018/8/6.
 */
public class FileBody implements ResponseBody {

    private File mBody;

    public FileBody(File body) {
        if (body == null) {
            throw new IllegalArgumentException("The file cannot be null.");
        }
        this.mBody = body;
    }

    @Override
    public boolean isRepeatable() {
        return true;
    }

    @Override
    public long contentLength() {
        return mBody.length();
    }

    @Nullable
    @Override
    public MediaType contentType() {
        return MediaType.getFileMediaType(mBody.getName());
    }

    @Override
    public void writeTo(@NonNull OutputStream output) throws IOException {
        InputStream is = new FileInputStream(mBody);
        IOUtils.write(is, output);
        IOUtils.closeQuietly(is);
    }
}