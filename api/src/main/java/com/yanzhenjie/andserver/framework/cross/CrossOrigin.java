/*
 * Copyright 2020 Zhenjie Yan.
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
package com.yanzhenjie.andserver.framework.cross;

import androidx.annotation.NonNull;

import com.yanzhenjie.andserver.http.HttpMethod;

/**
 * Created by Zhenjie Yan on 10/16/20.
 */
public class CrossOrigin {

    private String[] origins;
    private String[] allowedHeaders;
    private String[] exposedHeaders;
    private HttpMethod[] methods;
    private boolean allowCredentials;
    private long maxAge;

    public CrossOrigin() {
    }

    @NonNull
    public String[] getOrigins() {
        return origins;
    }

    public void setOrigins(String[] origins) {
        this.origins = origins;
    }

    @NonNull
    public String[] getAllowedHeaders() {
        return allowedHeaders;
    }

    public void setAllowedHeaders(String[] allowedHeaders) {
        this.allowedHeaders = allowedHeaders;
    }

    @NonNull
    public String[] getExposedHeaders() {
        return exposedHeaders;
    }

    public void setExposedHeaders(String[] exposedHeaders) {
        this.exposedHeaders = exposedHeaders;
    }

    @NonNull
    public HttpMethod[] getMethods() {
        return methods;
    }

    public void setMethods(HttpMethod[] methods) {
        this.methods = methods;
    }

    @NonNull
    public boolean isAllowCredentials() {
        return allowCredentials;
    }

    public void setAllowCredentials(boolean allowCredentials) {
        this.allowCredentials = allowCredentials;
    }

    @NonNull
    public long getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(long maxAge) {
        this.maxAge = maxAge;
    }
}