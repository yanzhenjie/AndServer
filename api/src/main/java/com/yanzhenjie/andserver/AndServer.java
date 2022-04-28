/*
 * Copyright (C) 2018 Zhenjie Yan.
 *               2022 ISNing
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

import com.yanzhenjie.andserver.server.async.AsyncReverseProxyServer;
import com.yanzhenjie.andserver.server.async.AsyncWebServer;
import com.yanzhenjie.andserver.server.async.H2ReverseProxyServer;
import com.yanzhenjie.andserver.server.async.H2WebServer;
import com.yanzhenjie.andserver.server.classic.ClassicReverseProxyServer;
import com.yanzhenjie.andserver.server.classic.ClassicWebServer;

/**
 * Created by Zhenjie Yan on 2018/6/9.
 */
public class AndServer {

    public static final String TAG = "AndServer";
    public static final String INFO = String.format("AndServer/%1$s", BuildConfig.PROJECT_VERSION);
    public static final String TAG_TEMPLATE = TAG + "/%1$s";

    /**
     * Generate logging tag for AndServer components
     *
     * @return Tag.
     */
    @NonNull
    public static String genAndServerTag(@NonNull String subTag) {
        return String.format(TAG_TEMPLATE, subTag);
    }

    /**
     * Create a builder for the web server.
     *
     * @return {@link ClassicServer.Builder}.
     */
    @NonNull
    public static ClassicServer.Builder<?, ?> webServer(@NonNull Context context) {
        return webServer(context, "default");
    }

    /**
     * Create a builder for the web server.
     *
     * @param group group name.
     * @return {@link ClassicServer.Builder}.
     */
    @NonNull
    public static ClassicServer.Builder<?, ?> webServer(@NonNull Context context,
                                                        @NonNull String group) {
        return ClassicWebServer.newBuilder(context, group);
    }

    /**
     * Create a builder for the asynchronous web server.
     *
     * @return {@link AsyncServer.Builder}.
     */
    @NonNull
    public static AsyncServer.Builder<?, ?> webServerAsync(@NonNull Context context) {
        return webServerAsync(context, "default");
    }

    /**
     * Create a builder for the asynchronous web server.
     *
     * @param group group name.
     * @return {@link AsyncServer.Builder}.
     */
    @NonNull
    public static AsyncServer.Builder<?, ?> webServerAsync(@NonNull Context context,
                                                           @NonNull String group) {
        return AsyncWebServer.newBuilder(context, group);
    }

    /**
     * Create a builder for the asynchronous web server.
     *
     * @return {@link H2Server.Builder}.
     */
    @NonNull
    public static H2Server.Builder<?, ?> webServerH2(@NonNull Context context) {
        return webServerH2(context, "default");
    }

    /**
     * Create a builder for the asynchronous web server.
     *
     * @param group group name.
     * @return {@link H2Server.Builder}.
     */
    @NonNull
    public static H2Server.Builder<?, ?> webServerH2(@NonNull Context context,
                                                     @NonNull String group) {
        return H2WebServer.newBuilder(context, group);
    }

    /**
     * Create a builder for the reverse proxy server.
     *
     * @return {@link ClassicServer.ProxyBuilder}.
     */
    @NonNull
    public static ClassicServer.ProxyBuilder<?, ?> proxyServer() {
        return ClassicReverseProxyServer.newBuilder();
    }

    /**
     * Create a builder for the asynchronous reverse proxy server.
     *
     * @return {@link AsyncServer.ProxyBuilder}.
     */
    @NonNull
    public static AsyncServer.ProxyBuilder<?, ?> proxyServerAsync() {
        return AsyncReverseProxyServer.newBuilder();
    }

    /**
     * Create a builder for the asynchronous reverse proxy server.
     *
     * @return {@link H2Server.ProxyBuilder}.
     */
    @NonNull
    public static H2Server.ProxyBuilder<?, ?> proxyServerH2() {
        return H2ReverseProxyServer.newBuilder();
    }

    /**
     * @deprecated use {@link #webServer(Context)} instead.
     */
    @NonNull
    @Deprecated
    public static ClassicServer.Builder<?, ?> serverBuilder(@NonNull Context context) {
        return webServer(context);
    }

    /**
     * @deprecated use {@link #webServer(Context, String)} instead.
     */
    @NonNull
    @Deprecated
    public static ClassicServer.Builder<?, ?> serverBuilder(@NonNull Context context, @NonNull String group) {
        return webServer(context, group);
    }
}