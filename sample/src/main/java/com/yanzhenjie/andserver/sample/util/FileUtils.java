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
package com.yanzhenjie.andserver.sample.util;

import android.os.Environment;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import com.yanzhenjie.andserver.http.multipart.MultipartFile;
import com.yanzhenjie.andserver.sample.App;

import java.io.File;
import java.util.UUID;

/**
 * Created by Zhenjie Yan on 2018/6/9.
 */
public class FileUtils {

    /**
     * Create a random file based on mimeType.
     *
     * @param file file.
     *
     * @return file object.
     */
    public static File createRandomFile(MultipartFile file) {
        String extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(file.getContentType().toString());
        if (TextUtils.isEmpty(extension)) {
            extension = MimeTypeMap.getFileExtensionFromUrl(file.getFilename());
        }
        String uuid = UUID.randomUUID().toString();
        return new File(App.getInstance().getRootDir(), uuid + "." + extension);
    }

    /**
     * SD is available.
     *
     * @return true, otherwise is false.
     */
    public static boolean storageAvailable() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File sd = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
            return sd.canWrite();
        } else {
            return false;
        }
    }
}