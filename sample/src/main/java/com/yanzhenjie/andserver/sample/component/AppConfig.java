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
package com.yanzhenjie.andserver.sample.component;

import android.content.Context;

import com.yanzhenjie.andserver.annotation.Config;
import com.yanzhenjie.andserver.framework.config.Multipart;
import com.yanzhenjie.andserver.framework.config.WebConfig;
import com.yanzhenjie.andserver.framework.website.AssetsWebsite;

import java.io.File;

/**
 * Created by Zhenjie Yan on 2019-06-30.
 */
@Config
public class AppConfig implements WebConfig {

    @Override
    public void onConfig(Context context, Delegate delegate) {
        delegate.addWebsite(new AssetsWebsite(context, "/web"));

        delegate.setMultipart(Multipart.newBuilder()
            .allFileMaxSize(1024 * 1024 * 20) // 20M
            .fileMaxSize(1024 * 1024 * 5) // 5M
            .maxInMemorySize(1024 * 10) // 1024 * 10 bytes
            .uploadTempDir(new File(context.getCacheDir(), "_server_upload_cache_"))
            .build());
    }
}