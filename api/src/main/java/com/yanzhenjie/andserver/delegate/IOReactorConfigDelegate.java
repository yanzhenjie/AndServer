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

import org.apache.hc.core5.reactor.IOReactorConfig;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;

import java.net.SocketAddress;
import java.util.concurrent.TimeUnit;

public class IOReactorConfigDelegate {
    private final IOReactorConfig ioReactorConfig;

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public IOReactorConfigDelegate(IOReactorConfig ioReactorConfig) {
        this.ioReactorConfig = ioReactorConfig;
    }

    public static Builder custom() {
        return new Builder(IOReactorConfig.custom());
    }

    public static Builder copy(IOReactorConfigDelegate config) {
        return new Builder(IOReactorConfig.copy(config.getIOReactorConfig()));
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public IOReactorConfig getIOReactorConfig() {
        return ioReactorConfig;
    }

    public long getSelectInterval() {
        return ioReactorConfig.getSelectInterval().toMilliseconds();
    }

    public int getIoThreadCount() {
        return ioReactorConfig.getIoThreadCount();
    }

    public long getSoTimeoutMillis() {
        return ioReactorConfig.getSoTimeout().toMilliseconds();
    }

    public boolean isSoReuseAddress() {
        return ioReactorConfig.isSoReuseAddress();
    }

    public long getSoLingerMillis() {
        return ioReactorConfig.getSoLinger().toMilliseconds();
    }

    public boolean isSoKeepalive() {
        return ioReactorConfig.isSoKeepalive();
    }

    public boolean isTcpNoDelay() {
        return ioReactorConfig.isTcpNoDelay();
    }

    public int getTrafficClass() {
        return ioReactorConfig.getTrafficClass();
    }

    public int getSndBufSize() {
        return ioReactorConfig.getSndBufSize();
    }

    public int getRcvBufSize() {
        return ioReactorConfig.getRcvBufSize();
    }

    public int getBacklogSize() {
        return ioReactorConfig.getBacklogSize();
    }

    public SocketAddress getSocksProxyAddress() {
        return ioReactorConfig.getSocksProxyAddress();
    }

    public String getSocksProxyUsername() {
        return ioReactorConfig.getSocksProxyUsername();
    }

    public String getSocksProxyPassword() {
        return ioReactorConfig.getSocksProxyPassword();
    }

    @NonNull
    @Override
    public String toString() {
        return ioReactorConfig.toString();
    }

    public static class Builder {
        private final IOReactorConfig.Builder builder;

        @RestrictTo(RestrictTo.Scope.LIBRARY)
        private Builder(IOReactorConfig.Builder builder) {
            this.builder = builder;
        }

        public static int getDefaultMaxIOThreadCount() {
            return IOReactorConfig.Builder.getDefaultMaxIOThreadCount();
        }

        public static void setDefaultMaxIOThreadCount(int defaultMaxIOThreadCount) {
            IOReactorConfig.Builder.setDefaultMaxIOThreadCount(defaultMaxIOThreadCount);
        }

        public Builder setSelectInterval(long selectInterval, TimeUnit unit) {
            builder.setSelectInterval(TimeValue.of(selectInterval, unit));
            return this;
        }

        public Builder setIoThreadCount(int ioThreadCount) {
            builder.setIoThreadCount(ioThreadCount);
            return this;
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

        public Builder setTrafficClass(int trafficClass) {
            builder.setTrafficClass(trafficClass);
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

        public Builder setSocksProxyUsername(String socksProxyUsername) {
            builder.setSocksProxyUsername(socksProxyUsername);
            return this;
        }

        public Builder setSocksProxyPassword(String socksProxyPassword) {
            builder.setSocksProxyPassword(socksProxyPassword);
            return this;
        }

        public IOReactorConfigDelegate build() {
            return new IOReactorConfigDelegate(builder.build());
        }
    }
}
