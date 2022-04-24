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
package com.yanzhenjie.andserver.http;

import androidx.annotation.RestrictTo;

import org.apache.hc.core5.util.Args;

public enum URIScheme {

    HTTP(org.apache.hc.core5.http.URIScheme.HTTP), HTTPS(org.apache.hc.core5.http.URIScheme.HTTPS);

    public final org.apache.hc.core5.http.URIScheme scheme;

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    URIScheme(final org.apache.hc.core5.http.URIScheme scheme) {
        this.scheme = Args.notNull(scheme, "Scheme");
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public org.apache.hc.core5.http.URIScheme getScheme() {
        return scheme;
    }

    public String getId() {
        return scheme.getId();
    }

    public boolean same(final String scheme) {
        return this.scheme.getId().equalsIgnoreCase(scheme);
    }

    @Override
    public String toString() {
        return scheme.getId();
    }
}
