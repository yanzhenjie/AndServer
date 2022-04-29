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
package com.yanzhenjie.andserver.http2;

import androidx.annotation.RestrictTo;

public enum HttpVersionPolicy {

    FORCE_HTTP_1(org.apache.hc.core5.http2.HttpVersionPolicy.FORCE_HTTP_1),
    FORCE_HTTP_2(org.apache.hc.core5.http2.HttpVersionPolicy.FORCE_HTTP_2),
    NEGOTIATE(org.apache.hc.core5.http2.HttpVersionPolicy.NEGOTIATE);

    private final org.apache.hc.core5.http2.HttpVersionPolicy mapping;

    HttpVersionPolicy(org.apache.hc.core5.http2.HttpVersionPolicy mapping) {
        this.mapping = mapping;
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public org.apache.hc.core5.http2.HttpVersionPolicy wrapped() {
        return mapping;
    }
}