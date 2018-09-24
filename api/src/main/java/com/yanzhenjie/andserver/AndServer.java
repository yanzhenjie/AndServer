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
package com.yanzhenjie.andserver;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.support.annotation.NonNull;

import com.yanzhenjie.andserver.util.Assert;

/**
 * Created by YanZhenjie on 2018/6/9.
 */
public class AndServer {

    public static final String TAG = "AndServer";

    @SuppressLint("StaticFieldLeak")
    private static Context sContext;
    private static boolean sDebug;

    static void initialize(@NonNull Context context) {
        Assert.notNull(context, "The context must not be null.");

        if (sContext == null) {
            synchronized (AndServer.class) {
                if (sContext == null) {
                    sContext = context.getApplicationContext();
                    ApplicationInfo appInfo = context.getApplicationInfo();
                    sDebug = (appInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
                }
            }
        }
    }

    /**
     * Get the context of the current application.
     *
     * @return {@link Context}.
     */
    @NonNull
    public static Context getContext() {
        return sContext;
    }

    /**
     * Whether the application is in debug mode.
     *
     * @return true, otherwise is false.
     */
    public static boolean isDebug() {
        return sDebug;
    }

    /**
     * Create a Builder of Server.
     *
     * @return {@link Server.Builder}.
     */
    public static Server.Builder serverBuilder() {
        return Server.newBuilder();
    }
}