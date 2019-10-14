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

import com.yanzhenjie.andserver.util.IOUtils;
import com.yanzhenjie.andserver.util.MediaType;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItem;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

/**
 * Created by Zhenjie Yan on 2018/6/21.
 */
public class StandardMultipartFile implements MultipartFile, Serializable {

    private final FileItem fileItem;
    private final long size;

    /**
     * Create an instance wrapping the given FileItem.
     *
     * @param fileItem the FileItem to wrap.
     */
    public StandardMultipartFile(FileItem fileItem) {
        this.fileItem = fileItem;
        this.size = fileItem.getSize();
    }

    /**
     * Return the underlying {@link FileItem} instance. There is hardly any need to access this.
     */
    public final FileItem getFileItem() {
        return fileItem;
    }

    @NonNull
    @Override
    public String getName() {
        return this.fileItem.getFieldName();
    }

    @Nullable
    @Override
    public String getFilename() {
        String filename = this.fileItem.getName();
        if (filename == null) {
            // Should never happen.
            return "";
        }

        // Check for Unix-style path.
        int unixSep = filename.lastIndexOf("/");
        // Check for Windows-style path.
        int winSep = filename.lastIndexOf("\\");
        // Cut off at latest possible point.
        int pos = (winSep > unixSep ? winSep : unixSep);
        if (pos != -1) {
            // Any sort of path separator found...
            return filename.substring(pos + 1);
        } else {
            // A plain name.
            return filename;
        }
    }

    @NonNull
    @Override
    public MediaType getContentType() {
        String mimeType = fileItem.getContentType();
        MediaType mediaType;
        try {
            mediaType = MediaType.parseMediaType(mimeType);
        } catch (Exception e) {
            mediaType = MediaType.APPLICATION_OCTET_STREAM;
        }
        return mediaType;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public long getSize() {
        return size;
    }

    @Override
    public byte[] getBytes() {
        if (!isAvailable()) {
            throw new IllegalStateException("File has been moved - cannot be read again.");
        }
        byte[] bytes = this.fileItem.get();
        return (bytes != null ? bytes : new byte[0]);
    }

    @NonNull
    @Override
    public InputStream getStream() throws IOException {
        if (!isAvailable()) {
            throw new IllegalStateException("File has been moved - cannot be read again.");
        }
        InputStream inputStream = fileItem.getInputStream();
        return (inputStream != null ? inputStream : IOUtils.createEmptyInput());
    }

    @Override
    public void transferTo(@NonNull File dest) throws IOException, IllegalStateException {
        if (!isAvailable()) {
            throw new IllegalStateException("File has already been moved - cannot be transferred again.");
        }

        if (dest.exists() && !dest.delete()) {
            throw new IOException(
                "Destination file [" + dest.getAbsolutePath() + "] already exists and could not be deleted.");
        }

        try {
            fileItem.write(dest);
        } catch (FileUploadException ex) {
            throw new IllegalStateException(ex.getMessage(), ex);
        } catch (IllegalStateException ex) {
            // Pass through when coming from FileItem directly.
            throw ex;
        } catch (IOException ex) {
            // From I/O operations within FileItem.write.
            throw ex;
        } catch (Exception ex) {
            throw new IOException("File transfer failed", ex);
        }
    }

    /**
     * Determine whether the multipart content is still available. If a temporary file has been moved, the content is no
     * longer available.
     */
    protected boolean isAvailable() {
        // If in memory, it's available.
        if (fileItem.isInMemory()) {
            return true;
        }
        // Check actual existence of temporary file.
        if (fileItem instanceof DiskFileItem) {
            return ((DiskFileItem) fileItem).getStoreLocation().exists();
        }
        // Check whether current file size is different than original one.
        return (fileItem.getSize() == size);
    }
}