/*
 * Copyright 2018 Yan Zhenjie.
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

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.yanzhenjie.andserver.AndServer;
import com.yanzhenjie.andserver.error.MaxUploadSizeExceededException;
import com.yanzhenjie.andserver.error.MultipartException;
import com.yanzhenjie.andserver.http.HttpRequest;
import com.yanzhenjie.andserver.http.RequestBody;
import com.yanzhenjie.andserver.util.Assert;
import com.yanzhenjie.andserver.util.LinkedMultiValueMap;
import com.yanzhenjie.andserver.util.MediaType;
import com.yanzhenjie.andserver.util.MultiValueMap;
import com.yanzhenjie.andserver.util.StringUtils;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.io.Charsets;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by YanZhenjie on 2018/8/9.
 */
public class StandardMultipartResolver implements MultipartResolver {

    private DiskFileItemFactory mFileItemFactory;
    private FileUpload mFileUpload;

    public StandardMultipartResolver(Context context) {
        this.mFileItemFactory = new DiskFileItemFactory();
        this.mFileItemFactory.setRepository(new File(context.getCacheDir(), "andserver_upload"));
        this.mFileUpload = new FileUpload(mFileItemFactory);
    }

    /**
     * Set the maximum size (in bytes) allowed for uploading. -1 indicates no limit (the default).
     *
     * @param allFileMaxSize the maximum upload size allowed.
     *
     * @see FileUpload#setSizeMax(long)
     */
    public void setAllFileMaxSize(long allFileMaxSize) {
        this.mFileUpload.setSizeMax(allFileMaxSize);
    }

    /**
     * Set the maximum size (in bytes) allowed for each individual file. -1 indicates no limit (the default).
     *
     * @param fileMaxSize the maximum upload size per file.
     *
     * @see FileUpload#setFileSizeMax(long)
     */
    public void setFileMaxSize(long fileMaxSize) {
        this.mFileUpload.setFileSizeMax(fileMaxSize);
    }

    /**
     * Set the maximum allowed size (in bytes) before uploads are written to disk, default is 10240.
     *
     * @param maxInMemorySize the maximum in memory size allowed.
     *
     * @see DiskFileItemFactory#setSizeThreshold(int)
     */
    public void setMaxInMemorySize(int maxInMemorySize) {
        this.mFileItemFactory.setSizeThreshold(maxInMemorySize);
    }

    /**
     * Set the temporary directory where uploaded files get stored.
     */
    public void setUploadTempDir(File uploadTempDir) throws IOException {
        if (!uploadTempDir.exists() && !uploadTempDir.mkdirs()) {
            String message = "Given uploadTempDir [" + uploadTempDir + "] could not be created.";
            throw new IllegalArgumentException(message);
        }
        this.mFileItemFactory.setRepository(uploadTempDir);
    }

    @Override
    public boolean isMultipart(HttpRequest request) {
        if (!request.getMethod().allowBody()) return false;

        RequestBody body = request.getBody();
        return body != null && FileUploadBase.isMultipartContent(new BodyContext(body));
    }

    @Override
    public MultipartRequest resolveMultipart(HttpRequest request) throws MultipartException {
        if (request instanceof MultipartRequest) return (MultipartRequest)request;

        MultipartParsingResult result = parseRequest(request);
        return new StandardMultipartRequest(request, result.getMultipartFiles(), result.getMultipartParameters(),
            result.getMultipartContentTypes());
    }

    @Override
    public void cleanupMultipart(MultipartRequest request) {
        if (request != null) {
            try {
                MultiValueMap<String, MultipartFile> multipartFiles = request.getMultiFileMap();
                for (List<MultipartFile> files : multipartFiles.values()) {
                    for (MultipartFile file : files) {
                        if (file instanceof StandardMultipartFile) {
                            StandardMultipartFile cmf = (StandardMultipartFile)file;
                            cmf.getFileItem().delete();
                        }
                    }
                }
            } catch (Throwable ex) {
                Log.w(AndServer.TAG, "Failed to perform multipart cleanup for servlet request.");
            }
        }
    }

    /**
     * Parse the given request, resolving its multipart elements.
     *
     * @param request the request to parse.
     *
     * @return the parsing result.
     *
     * @throws MultipartException if multipart resolution failed.
     */
    private MultipartParsingResult parseRequest(HttpRequest request) throws MultipartException {
        String encoding = determineEncoding(request);
        FileUpload fileUpload = prepareFileUpload(encoding);
        try {
            RequestBody body = request.getBody();
            Assert.notNull(body, "The body cannot be null.");
            List<FileItem> fileItems = fileUpload.parseRequest(new BodyContext(body));
            return parseFileItems(fileItems, encoding);
        } catch (FileUploadBase.SizeLimitExceededException ex) {
            throw new MaxUploadSizeExceededException(fileUpload.getSizeMax(), ex);
        } catch (FileUploadBase.FileSizeLimitExceededException ex) {
            throw new MaxUploadSizeExceededException(fileUpload.getFileSizeMax(), ex);
        } catch (FileUploadException ex) {
            throw new MultipartException("Failed to parse multipart servlet request.", ex);
        }
    }

    /**
     * Determine the encoding for the given request.
     *
     * <p>The default implementation checks the request encoding, falling back to the default encoding specified for
     * this resolver.
     *
     * @param request current request
     *
     * @return the encoding for the request .
     */
    @NonNull
    private String determineEncoding(HttpRequest request) {
        MediaType mimeType = request.getContentType();
        if (mimeType == null) return Charsets.UTF_8.name();
        Charset charset = mimeType.getCharset();
        return charset == null ? Charsets.UTF_8.name() : charset.name();
    }

    /**
     * Determine an appropriate FileUpload instance for the given encoding.
     *
     * <p>Default implementation returns the shared FileUpload instance if the encoding matches, else creates a new
     * FileUpload instance with the same configuration other than the desired encoding.
     *
     * @param encoding the character encoding to use.
     *
     * @return an appropriate FileUpload instance.
     */
    private FileUpload prepareFileUpload(@NonNull String encoding) {
        FileUpload actualFileUpload = mFileUpload;
        if (!encoding.equalsIgnoreCase(mFileUpload.getHeaderEncoding())) {
            actualFileUpload = new FileUpload(mFileItemFactory);
            actualFileUpload.setSizeMax(mFileUpload.getSizeMax());
            actualFileUpload.setFileSizeMax(mFileUpload.getFileSizeMax());
            actualFileUpload.setHeaderEncoding(encoding);
        }
        return actualFileUpload;
    }

    /**
     * Parse the given List of Commons FileItems into a MultipartParsingResult, containing MultipartFile instances and a
     * Map of multipart parameter.
     *
     * @param fileItems the Commons FileItems to parse.
     * @param encoding the encoding to use for form fields.
     *
     * @return the MultipartParsingResult.
     *
     * @see StandardMultipartFile#StandardMultipartFile(FileItem)
     */
    protected MultipartParsingResult parseFileItems(List<FileItem> fileItems, String encoding) {
        MultiValueMap<String, MultipartFile> multipartFiles = new LinkedMultiValueMap<>();
        MultiValueMap<String, String> multipartParameters = new LinkedMultiValueMap<>();
        Map<String, String> multipartContentTypes = new HashMap<>();

        // Extract multipart files and multipart parameters.
        for (FileItem fileItem : fileItems) {
            if (fileItem.isFormField()) {
                String value;
                String partEncoding = determineEncoding(fileItem.getContentType(), encoding);
                if (partEncoding != null) {
                    try {
                        value = fileItem.getString(partEncoding);
                    } catch (UnsupportedEncodingException ex) {
                        value = fileItem.getString();
                    }
                } else {
                    value = fileItem.getString();
                }
                List<String> curParam = multipartParameters.get(fileItem.getFieldName());
                if (curParam == null) {
                    // Simple form field.
                    curParam = new LinkedList<>();
                    curParam.add(value);
                    multipartParameters.put(fileItem.getFieldName(), curParam);
                } else {
                    // Array of simple form fields.
                    curParam.add(value);
                }
                multipartContentTypes.put(fileItem.getFieldName(), fileItem.getContentType());
            } else {
                StandardMultipartFile file = createMultipartFile(fileItem);
                multipartFiles.add(file.getName(), file);
            }
        }
        return new MultipartParsingResult(multipartFiles, multipartParameters, multipartContentTypes);
    }

    /**
     * Create a {@link StandardMultipartFile} wrapper for the given Commons {@link FileItem}.
     *
     * @param fileItem the Commons FileItem to wrap.
     *
     * @return the corresponding StandardMultipartFile (potentially a custom subclass).
     */
    protected StandardMultipartFile createMultipartFile(FileItem fileItem) {
        return new StandardMultipartFile(fileItem);
    }

    private String determineEncoding(String contentTypeHeader, String defaultEncoding) {
        if (!StringUtils.hasText(contentTypeHeader)) {
            return defaultEncoding;
        }
        MediaType contentType = MediaType.parseMediaType(contentTypeHeader);
        Charset charset = contentType.getCharset();
        return charset != null ? charset.name() : defaultEncoding;
    }

    /**
     * Holder for a Map of MultipartFiles and a Map of multipart parameters.
     */
    protected static class MultipartParsingResult {

        private final MultiValueMap<String, MultipartFile> multipartFiles;
        private final MultiValueMap<String, String> multipartParameters;
        private final Map<String, String> multipartContentTypes;

        public MultipartParsingResult(MultiValueMap<String, MultipartFile> mpFiles,
            MultiValueMap<String, String> mpParams, Map<String, String> mpParamContentTypes) {
            this.multipartFiles = mpFiles;
            this.multipartParameters = mpParams;
            this.multipartContentTypes = mpParamContentTypes;
        }

        public MultiValueMap<String, MultipartFile> getMultipartFiles() {
            return this.multipartFiles;
        }

        public MultiValueMap<String, String> getMultipartParameters() {
            return this.multipartParameters;
        }

        public Map<String, String> getMultipartContentTypes() {
            return this.multipartContentTypes;
        }
    }
}