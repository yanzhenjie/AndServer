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

import org.apache.hc.core5.http.config.CharCodingConfig;

import java.nio.charset.Charset;
import java.nio.charset.CodingErrorAction;

public class CharCodingConfigDelegate {
    private final CharCodingConfig charCodingConfig;

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public CharCodingConfigDelegate(CharCodingConfig charCodingConfig) {
        this.charCodingConfig = charCodingConfig;
    }

    public static Builder custom() {
        return new Builder(CharCodingConfig.custom());
    }

    public static Builder copy(CharCodingConfig config) {
        return new Builder(CharCodingConfig.copy(config));
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public CharCodingConfig getCharCodingConfig() {
        return charCodingConfig;
    }

    public Charset getCharset() {
        return charCodingConfig.getCharset();
    }

    public CodingErrorAction getMalformedInputAction() {
        return charCodingConfig.getMalformedInputAction();
    }

    public CodingErrorAction getUnmappableInputAction() {
        return charCodingConfig.getUnmappableInputAction();
    }

    @NonNull
    @Override
    public String toString() {
        return charCodingConfig.toString();
    }

    public static class Builder {
        private final CharCodingConfig.Builder builder;

        @RestrictTo(RestrictTo.Scope.LIBRARY)
        private Builder(CharCodingConfig.Builder builder) {
            this.builder = builder;
        }

        public CharCodingConfig.Builder setCharset(Charset charset) {
            return builder.setCharset(charset);
        }

        public Builder setMalformedInputAction(CodingErrorAction malformedInputAction) {
            builder.setMalformedInputAction(malformedInputAction);
            return this;
        }

        public Builder setUnmappableInputAction(CodingErrorAction unmappableInputAction) {
            builder.setUnmappableInputAction(unmappableInputAction);
            return this;
        }

        public CharCodingConfigDelegate build() {
            return new CharCodingConfigDelegate(builder.build());
        }
    }
}