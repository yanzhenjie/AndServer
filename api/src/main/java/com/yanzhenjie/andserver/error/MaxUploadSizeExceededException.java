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
package com.yanzhenjie.andserver.error;

import com.yanzhenjie.andserver.http.StatusCode;

/**
 * Created by Zhenjie Yan on 2018/8/9.
 */
public class MaxUploadSizeExceededException extends HttpException {

    private final long mMaxSize;

    /**
     * Constructor for MaxUploadSizeExceededException.
     *
     * @param maxUploadSize the maximum upload size allowed.
     */
    public MaxUploadSizeExceededException(long maxUploadSize) {
        this(maxUploadSize, null);
    }

    /**
     * Constructor for MaxUploadSizeExceededException.
     *
     * @param maxSize the maximum upload size allowed.
     * @param ex root cause from multipart parsing API in use.
     */
    public MaxUploadSizeExceededException(long maxSize, Throwable ex) {
        super(StatusCode.SC_REQUEST_ENTITY_TOO_LARGE, "Maximum upload size of " + maxSize + " bytes exceeded", ex);
        this.mMaxSize = maxSize;
    }

    /**
     * Return the maximum upload size allowed.
     */
    public long getMaxSize() {
        return this.mMaxSize;
    }
}