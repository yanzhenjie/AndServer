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

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yanzhenjie.andserver.http.HttpRequest;
import com.yanzhenjie.andserver.http.RequestWrapper;
import com.yanzhenjie.andserver.util.LinkedMultiValueMap;
import com.yanzhenjie.andserver.util.MultiValueMap;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Zhenjie Yan on 2018/8/9.
 */
public class StandardMultipartRequest extends RequestWrapper implements MultipartRequest {

    private HttpRequest mRequest;

    private MultiValueMap<String, MultipartFile> mMultipartFiles;
    private MultiValueMap<String, String> mMultipartParameters;
    private Map<String, String> mMultipartContentTypes;

    public StandardMultipartRequest(@NonNull HttpRequest request, @NonNull MultiValueMap<String, MultipartFile> mpFiles,
                                    @NonNull MultiValueMap<String, String> mpParams,
                                    @NonNull Map<String, String> mpContentTypes) {
        super(request);
        this.mRequest = request;
        this.mMultipartFiles = new LinkedMultiValueMap<>(Collections.unmodifiableMap(mpFiles));
        this.mMultipartParameters = new LinkedMultiValueMap<>(Collections.unmodifiableMap(mpParams));
        this.mMultipartContentTypes = Collections.unmodifiableMap(mpContentTypes);
    }

    @NonNull
    @Override
    public Iterator<String> getFileNames() {
        return mMultipartFiles.keySet().iterator();
    }

    @Nullable
    @Override
    public MultipartFile getFile(String name) {
        return mMultipartFiles.getFirst(name);
    }

    @Nullable
    @Override
    public List<MultipartFile> getFiles(String name) {
        List<MultipartFile> multipartFiles = mMultipartFiles.get(name);
        if (multipartFiles != null) {
            return multipartFiles;
        } else {
            return Collections.emptyList();
        }
    }

    @NonNull
    @Override
    public Map<String, MultipartFile> getFileMap() {
        return mMultipartFiles.toSingleValueMap();
    }

    @NonNull
    @Override
    public MultiValueMap<String, MultipartFile> getMultiFileMap() {
        return mMultipartFiles;
    }

    @Nullable
    @Override
    public String getMultipartContentType(String paramOrFileName) {
        MultipartFile file = getFile(paramOrFileName);
        return file == null ? mMultipartContentTypes.get(paramOrFileName) : file.getContentType().getType();
    }

    @NonNull
    @Override
    public List<String> getParameterNames() {
        if (mMultipartParameters.isEmpty()) {
            return mRequest.getParameterNames();
        }

        List<String> paramNames = new LinkedList<>();
        List<String> names = mRequest.getParameterNames();
        if (!names.isEmpty()) {
            paramNames.addAll(names);
        }
        paramNames.addAll(mMultipartParameters.keySet());
        return paramNames;
    }

    @Nullable
    @Override
    public String getParameter(@NonNull String name) {
        String value = mMultipartParameters.getFirst(name);
        return TextUtils.isEmpty(value) ? mRequest.getParameter(name) : value;
    }

    @NonNull
    @Override
    public List<String> getParameters(@NonNull String name) {
        List<String> values = mMultipartParameters.get(name);
        return values == null ? mRequest.getParameters(name) : values;
    }

    @NonNull
    @Override
    public MultiValueMap<String, String> getParameter() {
        return mMultipartParameters.isEmpty() ? mRequest.getParameter() : mMultipartParameters;
    }
}