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

import java.io.IOException;
import java.net.SocketAddress;

public interface ListenerEndpoint {

    /**
     * Returns the socket address of this endpoint.
     *
     * @return socket address.
     */
    SocketAddress getAddress();

    /**
     * Determines if this endpoint has been closed and is no longer listens
     * for incoming connections.
     *
     * @return {@code true} if the endpoint has been closed,
     * {@code false} otherwise.
     */
    boolean isClosed();

    void close(boolean immediate);

    void close() throws IOException;

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    default org.apache.hc.core5.reactor.ListenerEndpoint wrapped() {
        return new org.apache.hc.core5.reactor.ListenerEndpoint() {

            @Override
            public void close() throws IOException {
                ListenerEndpoint.this.close();
            }

            @Override
            public void close(CloseMode closeMode) {
                ListenerEndpoint.this.close(closeMode == CloseMode.IMMEDIATE);
            }

            @Override
            public SocketAddress getAddress() {
                return ListenerEndpoint.this.getAddress();
            }

            @Override
            public boolean isClosed() {
                return ListenerEndpoint.this.isClosed();
            }
        };
    }
}
