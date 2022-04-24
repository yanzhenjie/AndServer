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

import org.apache.hc.core5.io.CloseMode;
import org.apache.hc.core5.util.Timeout;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLSession;

public class HttpConnectionWrapper implements HttpConnection {
    private final org.apache.hc.core5.http.HttpConnection connection;

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public HttpConnectionWrapper(org.apache.hc.core5.http.HttpConnection connection) {
        this.connection = connection;
    }

    @Override
    public void close() throws IOException {
        connection.close();
    }

    @Override
    public SocketAddress getLocalAddress() {
        return connection.getLocalAddress();
    }

    @Override
    public long getEndpointRequestCount() {
        return connection.getEndpointDetails().getRequestCount();
    }

    @Override
    public long getEndpointResponseCount() {
        return connection.getEndpointDetails().getResponseCount();
    }

    @Override
    public long getEndpointSentBytesCount() {
        return connection.getEndpointDetails().getSentBytesCount();
    }

    @Override
    public long getEndpointReceivedBytesCount() {
        return connection.getEndpointDetails().getReceivedBytesCount();
    }

    @Override
    public SocketAddress getRemoteAddress() {
        return connection.getRemoteAddress();
    }

    @Override
    public String getProtocol() {
        return connection.getProtocolVersion().getProtocol();
    }

    @Override
    public int getProtocolVersionMajor() {
        return connection.getProtocolVersion().getMajor();
    }

    @Override
    public int getProtocolVersionMinor() {
        return connection.getProtocolVersion().getMinor();
    }

    @Override
    public SSLSession getSSLSession() {
        return connection.getSSLSession();
    }

    @Override
    public boolean isOpen() {
        return connection.isOpen();
    }

    @Override
    public long getSocketTimeoutMillis() {
        return connection.getSocketTimeout().toMilliseconds();
    }

    @Override
    public void setSocketTimeout(long duration, TimeUnit unit) {
        connection.setSocketTimeout(Timeout.of(duration, unit));
    }

    @Override
    public void close(boolean immediate) {
        connection.close(immediate ? CloseMode.IMMEDIATE : CloseMode.GRACEFUL);
    }
}
