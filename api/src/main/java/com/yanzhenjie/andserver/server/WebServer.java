/*
 * Copyright 2020 Zhenjie Yan.
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
package com.yanzhenjie.andserver.server;

import android.content.Context;

import com.yanzhenjie.andserver.ComponentRegister;
import com.yanzhenjie.andserver.DispatcherHandler;
import com.yanzhenjie.andserver.Server;

import org.apache.httpcore.protocol.HttpRequestHandler;

/**
 * Created by Zhenjie Yan on 3/7/20.
 */
public class WebServer extends BasicServer<WebServer.Builder> {

    public static Builder newBuilder(Context context, String group) {
        return new Builder(context, group);
    }

    private Context mContext;
    private String mGroup;

    private WebServer(Builder builder) {
        super(builder);
        this.mContext = builder.context;
        this.mGroup = builder.group;
    }

    @Override
    protected HttpRequestHandler requestHandler() {
        DispatcherHandler handler = new DispatcherHandler(mContext);
        ComponentRegister register = new ComponentRegister(mContext);
        try {
            register.register(handler, mGroup);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return handler;
    }

    public static class Builder extends BasicServer.Builder<Builder, WebServer>
        implements Server.Builder<Builder, WebServer> {

        private Context context;
        private String group;

        private Builder(Context context, String group) {
            this.context = context;
            this.group = group;
        }

        @Override
        public WebServer build() {
            return new WebServer(this);
        }
    }
}