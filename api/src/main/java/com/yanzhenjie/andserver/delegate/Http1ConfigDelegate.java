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
package com.yanzhenjie.andserver.delegate;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;

import org.apache.hc.core5.http.config.Http1Config;
import org.apache.hc.core5.util.Timeout;

import java.util.concurrent.TimeUnit;

public class Http1ConfigDelegate {
    private final Http1Config http1Config;

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public Http1ConfigDelegate(Http1Config http1Config) {
        this.http1Config = http1Config;
    }

    public static Builder custom() {
        return new Builder(Http1Config.custom());
    }

    public static Builder copy(Http1Config config) {
        return new Builder(Http1Config.copy(config));
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public Http1Config getHttp1Config() {
        return http1Config;
    }

    public int getBufferSize() {
        return http1Config.getBufferSize();
    }

    public int getChunkSizeHint() {
        return http1Config.getChunkSizeHint();
    }

    public long getWaitForContinueTimeout() {
        return http1Config.getWaitForContinueTimeout().toMilliseconds();
    }

    public int getMaxLineLength() {
        return http1Config.getMaxLineLength();
    }

    public int getMaxHeaderCount() {
        return http1Config.getMaxHeaderCount();
    }

    public int getMaxEmptyLineCount() {
        return http1Config.getMaxEmptyLineCount();
    }

    public int getInitialWindowSize() {
        return http1Config.getInitialWindowSize();
    }

    @NonNull
    @Override
    public String toString() {
        return http1Config.toString();
    }

    public static class Builder {
        private final Http1Config.Builder builder;

        @RestrictTo(RestrictTo.Scope.LIBRARY)
        private Builder(Http1Config.Builder builder) {
            this.builder = builder;
        }

        public Builder setBufferSize(int bufferSize) {
            builder.setBufferSize(bufferSize);
            return this;
        }

        public Builder setChunkSizeHint(int chunkSizeHint) {
            builder.setChunkSizeHint(chunkSizeHint);
            return this;
        }

        public Builder setWaitForContinueTimeout(long duration, TimeUnit unit) {
            builder.setWaitForContinueTimeout(Timeout.of(duration, unit));
            return this;
        }

        public Builder setMaxLineLength(int maxLineLength) {
            builder.setMaxLineLength(maxLineLength);
            return this;
        }

        public Builder setMaxHeaderCount(int maxHeaderCount) {
            builder.setMaxHeaderCount(maxHeaderCount);
            return this;
        }

        public Builder setMaxEmptyLineCount(int maxEmptyLineCount) {
            builder.setMaxEmptyLineCount(maxEmptyLineCount);
            return this;
        }

        public Builder setInitialWindowSize(int initialWindowSize) {
            builder.setInitialWindowSize(initialWindowSize);
            return this;
        }

        public Http1ConfigDelegate build() {
            return new Http1ConfigDelegate(builder.build());
        }
    }
}