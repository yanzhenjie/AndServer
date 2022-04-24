/*
 * Copyright (C) 2022 ISNing
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
package com.yanzhenjie.andserver.server.async;

import android.content.Context;

import androidx.annotation.NonNull;

import com.yanzhenjie.andserver.AsyncServer;
import com.yanzhenjie.andserver.ComponentRegister;
import com.yanzhenjie.andserver.handler.async.AsyncDispatcherHandler;

import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.hc.core5.function.Supplier;
import org.apache.hc.core5.http.nio.AsyncServerExchangeHandler;

import java.util.Collection;
import java.util.Collections;

/**
 * Created by ISNing on 2022/4/23
 */
public class AsyncWebServer extends BasicAsyncServer<AsyncWebServer.Builder> {

    private final Context mContext;
    private final String mGroup;

    private AsyncWebServer(Builder builder) {
        super(builder);
        this.mContext = builder.context;
        this.mGroup = builder.group;
    }

    public static Builder newBuilder(Context context, String group) {
        return new Builder(context, group);
    }

    @NonNull
    @Override
    protected Collection<ImmutableTriple<String, String, Supplier<AsyncServerExchangeHandler>>> requestHandlers() {
        return Collections.singletonList(new ImmutableTriple<>(null, "*", () -> {
            AsyncDispatcherHandler handler = new AsyncDispatcherHandler(mContext);
            ComponentRegister register = new ComponentRegister(mContext);
            try {
                register.register(handler, mGroup);
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            return handler;
        }));
    }

    public static class Builder extends BasicAsyncServer.Builder<Builder, AsyncWebServer>
            implements AsyncServer.Builder<Builder, AsyncWebServer> {

        private final Context context;
        private final String group;

        private Builder(Context context, String group) {
            this.context = context;
            this.group = group;
        }

        @Override
        public AsyncWebServer build() {
            return new AsyncWebServer(this);
        }
    }
}