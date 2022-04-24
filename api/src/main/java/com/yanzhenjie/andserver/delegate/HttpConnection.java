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

import org.apache.hc.core5.http.EndpointDetails;
import org.apache.hc.core5.http.ProtocolVersion;
import org.apache.hc.core5.io.CloseMode;
import org.apache.hc.core5.util.Timeout;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLSession;

public interface HttpConnection {
    long getEndpointRequestCount();

    long getEndpointResponseCount();

    long getEndpointSentBytesCount();

    long getEndpointReceivedBytesCount();

    String getProtocol();

    int getProtocolVersionMajor();

    int getProtocolVersionMinor();

    long getSocketTimeoutMillis();

    void close() throws IOException;

    SocketAddress getLocalAddress();

    SocketAddress getRemoteAddress();

    SSLSession getSSLSession();

    boolean isOpen();

    void setSocketTimeout(long duration, TimeUnit unit);

    void close(boolean immediate);

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    default org.apache.hc.core5.http.HttpConnection wrapped() {
        return new org.apache.hc.core5.http.HttpConnection() {

            @Override
            public void close(CloseMode closeMode) {
                HttpConnection.this.close(closeMode == CloseMode.IMMEDIATE);
            }

            @Override
            public Timeout getSocketTimeout() {
                return Timeout.ofMilliseconds(getSocketTimeoutMillis());
            }

            @Override
            public void setSocketTimeout(Timeout timeout) {
                HttpConnection.this.setSocketTimeout(timeout.getDuration(), timeout.getTimeUnit());
            }

            @Override
            public void close() throws IOException {
                HttpConnection.this.close();
            }

            @Override
            public EndpointDetails getEndpointDetails() {
                return new EndpointDetails(HttpConnection.this.getRemoteAddress(),
                        HttpConnection.this.getLocalAddress(), getSocketTimeout()) {
                    @Override
                    public long getRequestCount() {
                        return HttpConnection.this.getEndpointRequestCount();
                    }

                    @Override
                    public long getResponseCount() {
                        return HttpConnection.this.getEndpointResponseCount();
                    }

                    @Override
                    public long getSentBytesCount() {
                        return HttpConnection.this.getEndpointSentBytesCount();
                    }

                    @Override
                    public long getReceivedBytesCount() {
                        return HttpConnection.this.getEndpointReceivedBytesCount();
                    }
                };
            }

            @Override
            public SocketAddress getLocalAddress() {
                return HttpConnection.this.getLocalAddress();
            }

            @Override
            public SocketAddress getRemoteAddress() {
                return HttpConnection.this.getRemoteAddress();
            }

            @Override
            public ProtocolVersion getProtocolVersion() {
                return new ProtocolVersion(HttpConnection.this.getProtocol(),
                        HttpConnection.this.getProtocolVersionMajor(),
                        HttpConnection.this.getProtocolVersionMinor());
            }

            @Override
            public SSLSession getSSLSession() {
                return HttpConnection.this.getSSLSession();
            }

            @Override
            public boolean isOpen() {
                return HttpConnection.this.isOpen();
            }
        };
    }
}
