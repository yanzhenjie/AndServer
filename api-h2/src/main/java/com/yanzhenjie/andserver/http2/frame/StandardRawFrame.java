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

import java.nio.ByteBuffer;

public class StandardRawFrame extends Frame<ByteBuffer> implements RawFrame {

    private final ByteBuffer payload;
    private final int len;

    public StandardRawFrame(final int type, final int flags, final int streamId, final ByteBuffer payload) {
        super(type, flags, streamId);
        this.payload = payload;
        this.len = payload != null ? payload.remaining() : 0;
    }

    public boolean isPadded() {
        return isFlagSet(FrameFlag.PADDED);
    }

    public int getLength() {
        return len;
    }

    public ByteBuffer getPayloadContent() {
        if (payload != null) {
            if (isPadded()) {
                final ByteBuffer dup = payload.duplicate();
                if (dup.remaining() == 0) {
                    return null;
                }
                final int padding = dup.get() & 0xff;
                if (padding > dup.remaining()) {
                    return null;
                }
                dup.limit(dup.limit() - padding);
                return dup;
            }
            return payload.duplicate();
        }
        return null;
    }

    @Override
    public ByteBuffer getPayload() {
        return payload != null ? payload.duplicate() : null;
    }
}
