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

import java.nio.ByteBuffer;

public class RawFrameWrapper implements RawFrame {
    private final org.apache.hc.core5.http2.frame.RawFrame frame;

    public RawFrameWrapper(org.apache.hc.core5.http2.frame.RawFrame frame) {
        super();
        this.frame = frame;
    }

    @Override
    public boolean isPadded() {
        return frame.isPadded();
    }

    @Override
    public int getLength() {
        return frame.getLength();
    }

    @Override
    public ByteBuffer getPayloadContent() {
        return frame.getPayloadContent();
    }

    @Override
    public ByteBuffer getPayload() {
        return frame.getPayload();
    }

    @Override
    public boolean isType(FrameType type) {
        return frame.isType(type.wrapped());
    }

    @Override
    public boolean isFlagSet(FrameFlag flag) {
        return frame.isFlagSet(flag.wrapped());
    }

    @Override
    public int getType() {
        return frame.getType();
    }

    @Override
    public int getFlags() {
        return frame.getFlags();
    }

    @Override
    public int getStreamId() {
        return frame.getStreamId();
    }

    @NonNull
    @Override
    public String toString() {
        return frame.toString();
    }
}
