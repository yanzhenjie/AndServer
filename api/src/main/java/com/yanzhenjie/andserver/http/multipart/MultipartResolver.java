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

import com.yanzhenjie.andserver.error.MultipartException;
import com.yanzhenjie.andserver.http.HttpRequest;

import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;

import java.io.File;

/**
 * Created by Zhenjie Yan on 2018/8/8.
 */
public interface MultipartResolver {

    /**
     * Set the maximum size (in bytes) allowed for uploading. -1 indicates no limit (the default).
     *
     * @param allFileMaxSize the maximum upload size allowed.
     *
     * @see FileUpload#setSizeMax(long)
     */
    void setAllFileMaxSize(long allFileMaxSize);

    /**
     * Set the maximum size (in bytes) allowed for each individual file. -1 indicates no limit (the default).
     *
     * @param fileMaxSize the maximum upload size per file.
     *
     * @see FileUpload#setFileSizeMax(long)
     */
    void setFileMaxSize(long fileMaxSize);

    /**
     * Set the maximum allowed size (in bytes) before uploads are written to disk, default is 10240.
     *
     * @param maxInMemorySize the maximum in memory size allowed.
     *
     * @see DiskFileItemFactory#setSizeThreshold(int)
     */
    void setMaxInMemorySize(int maxInMemorySize);

    /**
     * Set the temporary directory where uploaded files get stored.
     */
    void setUploadTempDir(File uploadTempDir);

    /**
     * Determine if the given request contains multipart content, will typically check for content type
     * "multipart/form-data".
     *
     * @param request the request to be evaluated.
     *
     * @return whether the request contains multipart content.
     */
    boolean isMultipart(HttpRequest request);

    /**
     * Parse the given request into multipart files and parameters, and wrap the request inside a {@link
     * MultipartRequest} object that provides access to file descriptors and makes contained parameters accessible via
     * the standard HttpRequest methods.
     *
     * @param request the request to wrap (must be of a multipart content type).
     *
     * @return the wrapped request.
     *
     * @throws MultipartException if the request is not multipart, or encounter other problems.
     */
    MultipartRequest resolveMultipart(HttpRequest request) throws MultipartException;

    /**
     * Cleanup any resources used for the multipart handling, like a storage for the uploaded files.
     *
     * @param request the request to cleanup resources for.
     */
    void cleanupMultipart(MultipartRequest request);
}