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

import androidx.annotation.RestrictTo;

public enum FrameFlag {

    END_STREAM(0x01, org.apache.hc.core5.http2.frame.FrameFlag.END_STREAM),
    ACK(0x01, org.apache.hc.core5.http2.frame.FrameFlag.ACK),
    END_HEADERS(0x04, org.apache.hc.core5.http2.frame.FrameFlag.END_HEADERS),
    PADDED(0x08, org.apache.hc.core5.http2.frame.FrameFlag.PADDED),
    PRIORITY(0x20, org.apache.hc.core5.http2.frame.FrameFlag.PRIORITY);

    final int value;
    private final org.apache.hc.core5.http2.frame.FrameFlag mapping;

    FrameFlag(final int value, org.apache.hc.core5.http2.frame.FrameFlag mapping) {
        this.value = value;
        this.mapping = mapping;
    }

    public int getValue() {
        return value;
    }

    public static int of(final FrameFlag... flags) {
        org.apache.hc.core5.http2.frame.FrameFlag[] frameFlags = new org.apache.hc.core5.http2.frame.FrameFlag[flags.length];
        for (int i = 0; i < flags.length; i++)
            frameFlags[i] = flags[i] == null ? null : flags[i].wrapped();
        return org.apache.hc.core5.http2.frame.FrameFlag.of(frameFlags);
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public org.apache.hc.core5.http2.frame.FrameFlag wrapped() {
        return this.mapping;
    }
}