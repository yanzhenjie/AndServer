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

import androidx.annotation.RestrictTo;

import java.util.Set;

public class EntityDetailsWrapper implements EntityDetails {
    private final org.apache.hc.core5.http.EntityDetails entityDetails;

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public EntityDetailsWrapper(org.apache.hc.core5.http.EntityDetails entityDetails) {
        this.entityDetails = entityDetails;
    }

    @Override
    public long getContentLength() {
        return entityDetails.getContentLength();
    }

    @Override
    public String getContentType() {
        return entityDetails.getContentType();
    }

    @Override
    public String getContentEncoding() {
        return entityDetails.getContentEncoding();
    }

    @Override
    public boolean isChunked() {
        return entityDetails.isChunked();
    }

    @Override
    public Set<String> getTrailerNames() {
        return entityDetails.getTrailerNames();
    }
}
