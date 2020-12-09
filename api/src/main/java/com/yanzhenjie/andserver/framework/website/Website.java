/*
 * Copyright © 2018 Zhenjie Yan.
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
package com.yanzhenjie.andserver.framework.website;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yanzhenjie.andserver.framework.ETag;
import com.yanzhenjie.andserver.framework.LastModified;
import com.yanzhenjie.andserver.framework.handler.HandlerAdapter;
import com.yanzhenjie.andserver.framework.handler.RequestHandler;
import com.yanzhenjie.andserver.framework.view.BodyView;
import com.yanzhenjie.andserver.framework.view.View;
import com.yanzhenjie.andserver.http.HttpRequest;
import com.yanzhenjie.andserver.http.HttpResponse;
import com.yanzhenjie.andserver.http.ResponseBody;

import java.io.IOException;

/**
 * Created by Zhenjie Yan on 2018/9/4.
 */
public abstract class Website implements HandlerAdapter, ETag, LastModified {

    @Nullable
    @Override
    public String getETag(@NonNull HttpRequest request) throws Throwable {
        return null;
    }

    @Override
    public long getLastModified(@NonNull HttpRequest request) throws Throwable {
        return 0;
    }

    @Nullable
    @Override
    public RequestHandler getHandler(@NonNull HttpRequest request) {
        return new RequestHandler() {
            @Nullable
            @Override
            public String getETag(@NonNull HttpRequest request) throws Throwable {
                return Website.this.getETag(request);
            }

            @Override
            public long getLastModified(@NonNull HttpRequest request) throws Throwable {
                return Website.this.getLastModified(request);
            }

            @Override
            public View handle(@NonNull HttpRequest request, @NonNull HttpResponse response) throws Throwable {
                return new BodyView(getBody(request, response));
            }
        };
    }

    @NonNull
    public abstract ResponseBody getBody(@NonNull HttpRequest request, @NonNull HttpResponse response)
        throws IOException;
}