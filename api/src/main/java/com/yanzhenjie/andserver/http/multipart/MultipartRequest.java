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
import androidx.annotation.Nullable;

import com.yanzhenjie.andserver.http.HttpRequest;
import com.yanzhenjie.andserver.util.MultiValueMap;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * <p> This interface defines the multipart request access operations that are exposed for actual multipart requests.
 * </p>
 *
 * Created by Zhenjie Yan on 2018/6/21.
 */
public interface MultipartRequest extends HttpRequest {

    /**
     * <p> Return an {@link java.util.Iterator} of String objects containing the parameter names of the multipart files
     * contained in this request. These are the field names of the form (like with normal parameters), not the original
     * file names. </p>
     *
     * @return the names of the files.
     */
    @NonNull
    Iterator<String> getFileNames();

    /**
     * Return the contents plus description of an uploaded file in this request, or  null if it does not exist.
     *
     * @param name parameter name.
     *
     * @return a {@link MultipartFile} object.
     */
    @Nullable
    MultipartFile getFile(String name);

    /**
     * Return the contents plus description of uploaded files in this request, or an empty list if it does not exist.
     *
     * @param name parameter name.
     *
     * @return a {@link MultipartFile} list.
     */
    @Nullable
    List<MultipartFile> getFiles(String name);

    /**
     * Return a {@link java.util.Map} of the multipart files contained in this request.
     *
     * @return a map containing the parameter names as keys, and the {@link MultipartFile} objects as values.
     */
    @NonNull
    Map<String, MultipartFile> getFileMap();

    /**
     * Return a {@link MultiValueMap} of the multipart files contained in this request.
     *
     * @return a map containing the parameter names as keys, and a list of {@link MultipartFile} objects as values.
     */
    @NonNull
    MultiValueMap<String, MultipartFile> getMultiFileMap();

    /**
     * Determine the content type of the specified request part.
     *
     * @param paramOrFileName the name of the part.
     *
     * @return the associated content type, or null if not defined.
     */
    @Nullable
    String getMultipartContentType(String paramOrFileName);
}