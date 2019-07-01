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
package com.yanzhenjie.andserver.http;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by Zhenjie Yan on 2018/8/31.
 */
public class StandardContext implements HttpContext {

    private org.apache.httpcore.protocol.HttpContext mContext;

    public StandardContext(org.apache.httpcore.protocol.HttpContext context) {
        this.mContext = context;
    }

    @Nullable
    @Override
    public Object getAttribute(@NonNull String id) {
        return mContext.getAttribute(id);
    }

    @Override
    public void setAttribute(@NonNull String id, @NonNull Object obj) {
        mContext.setAttribute(id, obj);
    }

    @Nullable
    @Override
    public Object removeAttribute(@NonNull String id) {
        return mContext.removeAttribute(id);
    }
}