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

import java.util.Set;

public interface EntityDetails {

    /**
     * Returns length of the entity, if known.
     */
    long getContentLength();

    /**
     * Returns content type of the entity, if known.
     */
    String getContentType();

    /**
     * Returns content encoding of the entity, if known.
     */
    String getContentEncoding();

    /**
     * Returns chunked transfer hint for this entity.
     * <p>
     * The behavior of wrapping entities is implementation dependent,
     * but should respect the primary purpose.
     * </p>
     */
    boolean isChunked();

    /**
     * Preliminary declaration of trailing headers.
     */
    Set<String> getTrailerNames();

    default org.apache.hc.core5.http.EntityDetails wrapped() {
        return new org.apache.hc.core5.http.EntityDetails() {
            @Override
            public long getContentLength() {
                return EntityDetails.this.getContentLength();
            }

            @Override
            public String getContentType() {
                return EntityDetails.this.getContentType();
            }

            @Override
            public String getContentEncoding() {
                return EntityDetails.this.getContentEncoding();
            }

            @Override
            public boolean isChunked() {
                return EntityDetails.this.isChunked();
            }

            @Override
            public Set<String> getTrailerNames() {
                return EntityDetails.this.getTrailerNames();
            }
        };
    }
}
