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
package com.yanzhenjie.andserver;

import android.content.Context;

import androidx.annotation.NonNull;

import com.yanzhenjie.andserver.server.ProxyServer;
import com.yanzhenjie.andserver.server.WebServer;

/**
 * Created by Zhenjie Yan on 2018/6/9.
 */
public class AndServer {

    public static final String TAG = "AndServer";
    public static final String INFO = String.format("AndServer/%1$s", BuildConfig.PROJECT_VERSION);

    /**
     * Create a builder for the web server.
     *
     * @return {@link Server.Builder}.
     */
    @NonNull
    public static Server.Builder webServer(@NonNull Context context) {
        return WebServer.newBuilder(context, "default");
    }

    /**
     * Create a builder for the web server.
     *
     * @param group group name.
     *
     * @return {@link Server.Builder}.
     */
    @NonNull
    public static Server.Builder webServer(@NonNull Context context,
        @NonNull String group) {
        return WebServer.newBuilder(context, group);
    }

    /**
     * Create a builder for the reverse proxy server.
     *
     * @return {@link Server.ProxyBuilder}.
     */
    @NonNull
    public static Server.ProxyBuilder proxyServer() {
        return ProxyServer.newBuilder();
    }

    /**
     * @deprecated use {@link #webServer(Context)} instead.
     */
    @NonNull
    @Deprecated
    public static Server.Builder serverBuilder(@NonNull Context context) {
        return webServer(context);
    }

    /**
     * @deprecated use {@link #webServer(Context, String)} instead.
     */
    @NonNull
    @Deprecated
    public static Server.Builder serverBuilder(@NonNull Context context, @NonNull String group) {
        return webServer(context, group);
    }
}