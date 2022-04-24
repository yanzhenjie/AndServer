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
package com.yanzhenjie.andserver.delegate.ssl;

import androidx.annotation.RestrictTo;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;

public class BasicServerTlsStrategy implements TlsStrategy {
    private final SSLContext sslContext;

    @Override
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public org.apache.hc.core5.http.nio.ssl.TlsStrategy wrapped() {
        return new org.apache.hc.core5.http.nio.ssl.BasicServerTlsStrategy(sslContext);
    }

    public BasicServerTlsStrategy(SSLContext sslContext) {
        this.sslContext = sslContext;
    }

    public BasicServerTlsStrategy() throws NoSuchAlgorithmException, KeyManagementException {
        this.sslContext = SSLContexts.createSystemDefault();
    }
}
