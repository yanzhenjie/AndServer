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

import com.yanzhenjie.andserver.util.MediaType;

import org.apache.commons.fileupload.FileItem;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Zhenjie Yan on 2018/6/18.
 */
public interface MultipartFile {

    /**
     * Return the name of the parameter in the multipart form.
     *
     * @return the name of the parameter.
     */
    @NonNull
    String getName();

    /**
     * Return the original filename in the client's filesystem.
     *
     * @return the original filename, or the empty String if no file, or null if not defined.
     *
     * @see FileItem#getName()
     */
    @Nullable
    String getFilename();

    /**
     * Return the content type of the file.
     *
     * @return the content type, or the empty String if no file, or null if not defined.
     */
    @NonNull
    MediaType getContentType();

    /**
     * Return whether the uploaded file is empty.
     *
     * @return true, otherwise is false.
     */
    boolean isEmpty();

    /**
     * Return the size of the file in bytes.
     *
     * @return the size of the file, or 0 if empty.
     */
    long getSize();

    /**
     * Return the contents of the file as an array of bytes.
     *
     * @return the contents of the file as bytes, or an empty byte array if empty.
     *
     * @throws IOException in case of access errors (if the temporary store fails).
     */
    byte[] getBytes() throws IOException;

    /**
     * Return an {@code InputStream} to read the contents of the file from.
     *
     * @return the contents of the file as stream, or an empty stream if empty.
     *
     * @throws IOException in case of access errors.
     */
    @NonNull
    InputStream getStream() throws IOException;

    /**
     * Writing the received file to the given destination file. If the destination file already exists, it will be
     * deleted first.
     *
     * <p>If the target file has been written to disk, this operation cannot be invoked again afterwards.
     *
     * @param dest the destination file.
     */
    void transferTo(@NonNull File dest) throws IOException, IllegalStateException;
}