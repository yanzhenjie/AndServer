/*
 * Copyright © 2019 Zhenjie Yan.
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
package com.yanzhenjie.andserver.framework.config;

import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;

import java.io.File;

/**
 * Created by Zhenjie Yan on 2019-06-28.
 */
public class Multipart {

    public static Builder newBuilder() {
        return new Builder();
    }

    private final long allFileMaxSize;
    private final long fileMaxSize;
    private final int maxInMemorySize;
    private final File uploadTempDir;

    private Multipart(Builder builder) {
        this.allFileMaxSize = builder.allFileMaxSize;
        this.fileMaxSize = builder.fileMaxSize;
        this.maxInMemorySize = builder.maxInMemorySize;
        this.uploadTempDir = builder.uploadTempDir;
    }

    public long getAllFileMaxSize() {
        return allFileMaxSize;
    }

    public long getFileMaxSize() {
        return fileMaxSize;
    }

    public int getMaxInMemorySize() {
        return maxInMemorySize;
    }

    public File getUploadTempDir() {
        return uploadTempDir;
    }

    public static class Builder {

        private long allFileMaxSize;
        private long fileMaxSize;
        private int maxInMemorySize;
        private File uploadTempDir;

        private Builder() {
        }

        /**
         * Set the maximum size (in bytes) allowed for uploading. -1 indicates no limit (the default).
         *
         * @param allFileMaxSize the maximum upload size allowed.
         *
         * @see FileUpload#setSizeMax(long)
         */
        public Builder allFileMaxSize(long allFileMaxSize) {
            this.allFileMaxSize = allFileMaxSize;
            return this;
        }

        /**
         * Set the maximum size (in bytes) allowed for each individual file. -1 indicates no limit (the default).
         *
         * @param fileMaxSize the maximum upload size per file.
         *
         * @see FileUpload#setFileSizeMax(long)
         */
        public Builder fileMaxSize(long fileMaxSize) {
            this.fileMaxSize = fileMaxSize;
            return this;
        }

        /**
         * Set the maximum allowed size (in bytes) before uploads are written to disk, default is 10240.
         *
         * @param maxInMemorySize the maximum in memory size allowed.
         *
         * @see DiskFileItemFactory#setSizeThreshold(int)
         */
        public Builder maxInMemorySize(int maxInMemorySize) {
            this.maxInMemorySize = maxInMemorySize;
            return this;
        }

        /**
         * Set the temporary directory where uploaded files get stored.
         */
        public Builder uploadTempDir(File uploadTempDir) {
            this.uploadTempDir = uploadTempDir;
            return this;
        }

        public Multipart build() {
            return new Multipart(this);
        }

    }
}