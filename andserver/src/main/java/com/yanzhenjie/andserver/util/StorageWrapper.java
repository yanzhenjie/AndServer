/*
 * Copyright © Yan Zhenjie. All Rights Reserved
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
package com.yanzhenjie.andserver.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Storage file wrapper.</p>
 * Created by Yan Zhenjie on 2017/3/15.
 */

public class StorageWrapper {

    /**
     * Scan all files in the inPath.
     *
     * @param inPath path in the path.
     * @return under inPath absolute path.
     */
    public List<String> scanFile(String inPath) {
        List<String> pathList = new ArrayList<>(2);
        File file = new File(inPath);
        if (file.isFile()) pathList.add(inPath);
        else {
            File[] files = file.listFiles();
            if (files != null && files.length > 0) {
                for (File childFile : files) {
                    if (childFile.isFile()) pathList.add(childFile.getAbsolutePath());
                    else {
                        List<String> childPathList = scanFile(childFile.getAbsolutePath());
                        if (childPathList.size() > 0) pathList.addAll(childPathList);
                    }
                }
            }
        }
        return pathList;
    }

}
