/*
 * Copyright © 2017 Yan Zhenjie.
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
package com.yanzhenjie.andserver.website;

import com.yanzhenjie.andserver.exception.NotFoundException;
import com.yanzhenjie.andserver.protocol.ETag;
import com.yanzhenjie.andserver.protocol.LastModified;
import com.yanzhenjie.andserver.view.View;

import org.apache.httpcore.HttpEntity;
import org.apache.httpcore.HttpException;
import org.apache.httpcore.HttpRequest;
import org.apache.httpcore.entity.ContentType;
import org.apache.httpcore.entity.FileEntity;
import org.apache.httpcore.protocol.HttpContext;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import static com.yanzhenjie.andserver.util.FileUtils.getMimeType;
import static com.yanzhenjie.andserver.util.HttpRequestParser.getRequestPath;

/**
 * Created by YanZhenjie on 2017/12/23.
 */
public class FileBrowser extends SimpleWebsite implements LastModified, ETag {

    private final String mRootPath;

    public FileBrowser(String rootPath) {
        this.mRootPath = rootPath;
    }

    @Override
    public boolean intercept(HttpRequest request, HttpContext context) throws HttpException, IOException {
        String httpPath = getRequestPath(request);
        httpPath = "/".equals(httpPath) ? "/" : trimEndSlash(getRequestPath(request));
        File source = findPathSource(httpPath);
        return source != null;
    }

    /**
     * Find the path specified resource.
     *
     * @param httpPath path.
     * @return return if the file is found.
     */
    private File findPathSource(String httpPath) {
        if ("/".equals(httpPath)) {
            return new File(mRootPath);
        } else {
            File sourceFile = new File(mRootPath, httpPath);
            if (sourceFile.exists()) {
                return sourceFile;
            }
        }
        return null;
    }

    @Override
    public View handle(HttpRequest request) throws HttpException, IOException {
        String httpPath = trimEndSlash(getRequestPath(request));
        File source = findPathSource(httpPath);
        if (source == null)
            throw new NotFoundException(httpPath);
        return generatePageView(source);
    }

    /**
     * Generate {@code View} for page of folder.
     *
     * @param source folder.
     * @return view of folder.
     */
    private View generatePageView(File source) throws IOException {
        if (source.isDirectory()) {
            File[] files = source.listFiles();
            File tempFile = File.createTempFile("file_browser", ".html");
            OutputStream outputStream = new FileOutputStream(tempFile);

            String folderName = source.getName();
            String prefix = String.format(FOLDER_HTML_PREFIX, folderName, folderName);
            outputStream.write(prefix.getBytes("utf-8"));

            if (files != null && files.length > 0) {
                for (File file : files) {
                    String filePath = file.getAbsolutePath();
                    int rootIndex = filePath.indexOf(mRootPath);
                    String httpPath = filePath.substring(rootIndex + mRootPath.length());
                    httpPath = addStartSlash(httpPath);
                    String fileItem = String.format(FOLDER_ITEM, httpPath, file.getName());
                    outputStream.write(fileItem.getBytes("utf-8"));
                }
            }

            outputStream.write(FOLDER_HTML_SUFFIX.getBytes("utf-8"));
            return generateSourceView(tempFile);
        } else {
            return generateSourceView(source);
        }
    }

    /**
     * Generate {@code View} for source.
     *
     * @param source file.
     * @return view of source.
     */
    private View generateSourceView(File source) throws IOException {
        String mimeType = getMimeType(source.getAbsolutePath());
        HttpEntity httpEntity = new FileEntity(source, ContentType.create(mimeType, Charset.defaultCharset()));
        return new View(200, httpEntity);
    }

    @Override
    public long getLastModified(HttpRequest request) throws IOException {
        String httpPath = trimEndSlash(getRequestPath(request));
        File source = findPathSource(httpPath);
        if (source != null && source.isFile())
            return source.lastModified();
        return -1;
    }

    @Override
    public String getETag(HttpRequest request) throws IOException {
        String httpPath = trimEndSlash(getRequestPath(request));
        File source = findPathSource(httpPath);
        if (source != null && source.isFile()) {
            long sourceSize = source.length();
            String sourcePath = source.getAbsolutePath();
            long lastModified = source.lastModified();
            return sourceSize + sourcePath + lastModified;
        }
        return null;
    }

    private static final String FOLDER_HTML_PREFIX
            = "<!DOCTYPE html>"
            + "<html>"
            + "<head>"
            + "<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\"/>"
            + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1, user-scalable=no\">"
            + "<meta name=\"format-detection\" content=\"telephone=no\"/>"
            + "<title>%1$s</title>"
            + "<style>"
            + ".center_horizontal{margin:0 auto;text-align:center;}"
            + "*,*::after,*::before {"
            + "box-sizing: border-box;"
            + "margin: 0;"
            + "padding: 0;"
            + "}"
            + "a:-webkit-any-link {"
            + "color: -webkit-link;"
            + "cursor: auto;"
            + "text-decoration: underline;"
            + "}"
            + "ul {"
            + "list-style: none;"
            + "display: block;"
            + "list-style-type: none;"
            + "-webkit-margin-before: 1em;"
            + "-webkit-margin-after: 1em;"
            + "-webkit-margin-start: 0px;"
            + "-webkit-margin-end: 0px;"
            + "-webkit-padding-start: 40px;"
            + "}"
            + "li {"
            + "display: list-item;"
            + "text-align: -webkit-match-parent;"
            + "margin-bottom: 5px;"
            + "}"
            + "</style>"
            + "</head>"
            + "<body>"
            + "<h1 class=\"center_horizontal\">%2$s</h1>"
            + "<ul>";
    private static final String FOLDER_ITEM = "<li><a href=\"%1$s\">%2$s</a></li>";
    private static final String FOLDER_HTML_SUFFIX
            = "</ul>"
            + "</body>"
            + "</html>";
}