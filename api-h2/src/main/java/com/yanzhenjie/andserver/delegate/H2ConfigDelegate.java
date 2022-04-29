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

import org.apache.hc.core5.http2.config.H2Config;

public class H2ConfigDelegate {
    private final H2Config h2Config;

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public H2ConfigDelegate(H2Config h2Config) {
        this.h2Config = h2Config;
    }

    public static Builder custom() {
        return new Builder(H2Config.custom());
    }

    public static Builder initial() {
        return new Builder(H2Config.initial());
    }

    public static Builder copy(H2Config config) {
        return new Builder(H2Config.copy(config));
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public H2Config getH2Config() {
        return h2Config;
    }

    public int getHeaderTableSize() {
        return h2Config.getHeaderTableSize();
    }

    public boolean isPushEnabled() {
        return h2Config.isPushEnabled();
    }

    public int getMaxConcurrentStreams() {
        return h2Config.getMaxConcurrentStreams();
    }

    public int getInitialWindowSize() {
        return h2Config.getInitialWindowSize();
    }

    public int getMaxFrameSize() {
        return h2Config.getMaxFrameSize();
    }

    public int getMaxHeaderListSize() {
        return h2Config.getMaxHeaderListSize();
    }

    public boolean isCompressionEnabled() {
        return h2Config.isCompressionEnabled();
    }

    @NonNull
    @Override
    public String toString() {
        return h2Config.toString();
    }

    public static class Builder {
        private final H2Config.Builder builder;

        @RestrictTo(RestrictTo.Scope.LIBRARY)
        private Builder(H2Config.Builder builder) {
            this.builder = builder;
        }

        public Builder setHeaderTableSize(int headerTableSize) {
            builder.setHeaderTableSize(headerTableSize);
            return this;
        }

        public Builder setPushEnabled(boolean pushEnabled) {
            builder.setPushEnabled(pushEnabled);
            return this;
        }

        public Builder setMaxConcurrentStreams(int maxConcurrentStreams) {
            builder.setMaxConcurrentStreams(maxConcurrentStreams);
            return this;
        }

        public Builder setInitialWindowSize(int initialWindowSize) {
            builder.setInitialWindowSize(initialWindowSize);
            return this;
        }

        public Builder setMaxFrameSize(int maxFrameSize) {
            builder.setMaxFrameSize(maxFrameSize);
            return this;
        }

        public Builder setMaxHeaderListSize(int maxHeaderListSize) {
            builder.setMaxHeaderListSize(maxHeaderListSize);
            return this;
        }

        public Builder setCompressionEnabled(boolean compressionEnabled) {
            builder.setCompressionEnabled(compressionEnabled);
            return this;
        }

        public H2ConfigDelegate build() {
            return new H2ConfigDelegate(builder.build());
        }
    }
}
