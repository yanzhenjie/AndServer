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
package com.yanzhenjie.andserver.http2.frame;

import androidx.annotation.NonNull;

public abstract class Frame<T> {
    private final org.apache.hc.core5.http2.frame.Frame<T> frame;

    public abstract T getPayload();

    @NonNull
    @Override
    public String toString() {
        return frame.toString();
    }

    public Frame(final int type, final int flags, final int streamId) {
        this.frame = new org.apache.hc.core5.http2.frame.Frame<T>(type, flags, streamId) {
            @Override
            public T getPayload() {
                return Frame.this.getPayload();
            }
        };
    }

    public boolean isType(FrameType type) {
        return frame.isType(type.wrapped());
    }

    public boolean isFlagSet(FrameFlag flag) {
        return frame.isFlagSet(flag.wrapped());
    }

    public int getType() {
        return frame.getType();
    }

    public int getFlags() {
        return frame.getFlags();
    }

    public int getStreamId() {
        return frame.getStreamId();
    }
}
