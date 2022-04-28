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

public enum FrameType {

    DATA(0x00, org.apache.hc.core5.http2.frame.FrameType.DATA),
    HEADERS(0x01, org.apache.hc.core5.http2.frame.FrameType.HEADERS),
    PRIORITY(0x02, org.apache.hc.core5.http2.frame.FrameType.PRIORITY),
    RST_STREAM(0x03, org.apache.hc.core5.http2.frame.FrameType.RST_STREAM),
    SETTINGS(0x04, org.apache.hc.core5.http2.frame.FrameType.SETTINGS),
    PUSH_PROMISE(0x05, org.apache.hc.core5.http2.frame.FrameType.PUSH_PROMISE),
    PING(0x06, org.apache.hc.core5.http2.frame.FrameType.PING),
    GOAWAY(0x07, org.apache.hc.core5.http2.frame.FrameType.GOAWAY),
    WINDOW_UPDATE(0x08, org.apache.hc.core5.http2.frame.FrameType.WINDOW_UPDATE),
    CONTINUATION(0x09, org.apache.hc.core5.http2.frame.FrameType.CONTINUATION);

    private final int value;
    private final org.apache.hc.core5.http2.frame.FrameType mapping;

    FrameType(final int value, final org.apache.hc.core5.http2.frame.FrameType mapping) {
        this.value = value;
        this.mapping = mapping;
    }

    public int getValue() {
        return value;
    }

    private static final FrameType[] LOOKUP_TABLE = new FrameType[10];

    static {
        for (final FrameType frameType : FrameType.values()) {
            LOOKUP_TABLE[frameType.value] = frameType;
        }
    }

    public static FrameType valueOf(final int value) {
        if (value < 0 || value >= LOOKUP_TABLE.length) {
            return null;
        }
        return LOOKUP_TABLE[value];
    }

    public static String toString(final int value) {
        if (value < 0 || value >= LOOKUP_TABLE.length) {
            return Integer.toString(value);
        }
        return LOOKUP_TABLE[value].name();
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public org.apache.hc.core5.http2.frame.FrameType wrapped() {
        return mapping;
    }
}
