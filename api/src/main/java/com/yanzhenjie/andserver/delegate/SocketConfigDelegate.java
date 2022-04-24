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

import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;

import java.net.SocketAddress;
import java.util.concurrent.TimeUnit;

public class SocketConfigDelegate {
    private final SocketConfig socketConfig;

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public SocketConfigDelegate(SocketConfig socketConfig) {
        this.socketConfig = socketConfig;
    }

    public static Builder custom() {
        return new Builder(SocketConfig.custom());
    }

    public static Builder copy(SocketConfigDelegate config) {
        return new Builder(SocketConfig.copy(config.getSocketConfig()));
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public SocketConfig getSocketConfig() {
        return socketConfig;
    }

    public long getSoTimeoutMillis() {
        return socketConfig.getSoTimeout().toMilliseconds();
    }

    public boolean isSoReuseAddress() {
        return socketConfig.isSoReuseAddress();
    }

    public long getSoLingerMillis() {
        return socketConfig.getSoLinger().toMilliseconds();
    }

    public boolean isSoKeepAlive() {
        return socketConfig.isSoKeepAlive();
    }

    public boolean isTcpNoDelay() {
        return socketConfig.isTcpNoDelay();
    }

    public int getSndBufSize() {
        return socketConfig.getSndBufSize();
    }

    public int getRcvBufSize() {
        return socketConfig.getRcvBufSize();
    }

    public int getBacklogSize() {
        return socketConfig.getBacklogSize();
    }

    public SocketAddress getSocksProxyAddress() {
        return socketConfig.getSocksProxyAddress();
    }

    @NonNull
    @Override
    public String toString() {
        return socketConfig.toString();
    }

    public static class Builder {
        private final SocketConfig.Builder builder;

        @RestrictTo(RestrictTo.Scope.LIBRARY)
        private Builder(SocketConfig.Builder builder) {
            this.builder = builder;
        }

        public Builder setSoTimeout(long duration, TimeUnit unit) {
            builder.setSoTimeout(Timeout.of(duration, unit));
            return this;
        }

        public Builder setSoReuseAddress(boolean soReuseAddress) {
            builder.setSoReuseAddress(soReuseAddress);
            return this;
        }

        public Builder setSoLinger(long soLinger, TimeUnit unit) {
            builder.setSoLinger(TimeValue.of(soLinger, unit));
            return this;
        }

        public Builder setSoKeepAlive(boolean soKeepAlive) {
            builder.setSoKeepAlive(soKeepAlive);
            return this;
        }

        public Builder setTcpNoDelay(boolean tcpNoDelay) {
            builder.setTcpNoDelay(tcpNoDelay);
            return this;
        }

        public Builder setSndBufSize(int sndBufSize) {
            builder.setSndBufSize(sndBufSize);
            return this;
        }

        public Builder setRcvBufSize(int rcvBufSize) {
            builder.setRcvBufSize(rcvBufSize);
            return this;
        }

        public Builder setBacklogSize(int backlogSize) {
            builder.setBacklogSize(backlogSize);
            return this;
        }

        public Builder setSocksProxyAddress(SocketAddress socksProxyAddress) {
            builder.setSocksProxyAddress(socksProxyAddress);
            return this;
        }

        public SocketConfigDelegate build() {
            return new SocketConfigDelegate(builder.build());
        }
    }
}
