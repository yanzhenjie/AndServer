/*
 * Copyright (C) 2022 Zhenjie Yan
 *                    ISNing
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
package com.yanzhenjie.andserver.server.classic;

import android.content.Context;

import androidx.annotation.NonNull;

import com.yanzhenjie.andserver.ClassicServer;
import com.yanzhenjie.andserver.ComponentRegister;
import com.yanzhenjie.andserver.handler.classic.ClassicDispatcherHandler;

import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.hc.core5.http.io.HttpRequestHandler;

import java.util.Collections;
import java.util.List;

/**
 * Created by Zhenjie Yan on 3/7/20.
 */
public class ClassicWebServer extends BasicClassicServer<ClassicWebServer.Builder> {

    private final Context mContext;
    private final String mGroup;

    private ClassicWebServer(Builder builder) {
        super(builder);
        this.mContext = builder.context;
        this.mGroup = builder.group;
    }

    public static Builder newBuilder(Context context, String group) {
        return new Builder(context, group);
    }

    @NonNull
    @Override
    protected List<ImmutableTriple<String, String, HttpRequestHandler>> requestHandlers() {
        ClassicDispatcherHandler handler = new ClassicDispatcherHandler(mContext);
        ComponentRegister register = new ComponentRegister(mContext);
        try {
            register.register(handler, mGroup);
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return Collections.singletonList(new ImmutableTriple<>(null, "*", handler));
    }

    public static class Builder extends BasicClassicServer.Builder<Builder, ClassicWebServer>
            implements ClassicServer.Builder<Builder, ClassicWebServer> {

        private final Context context;
        private final String group;

        private Builder(Context context, String group) {
            this.context = context;
            this.group = group;
        }

        @Override
        public ClassicWebServer build() {
            return new ClassicWebServer(this);
        }
    }
}