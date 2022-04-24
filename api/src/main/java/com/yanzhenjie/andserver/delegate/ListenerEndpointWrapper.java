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

import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.io.CloseMode;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ListenerEndpointWrapper implements ListenerEndpoint {
    org.apache.hc.core5.reactor.ListenerEndpoint listenerEndpoint;

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public ListenerEndpointWrapper(org.apache.hc.core5.reactor.ListenerEndpoint listenerEndpoint) {
        this.listenerEndpoint = listenerEndpoint;
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public static Future<ListenerEndpoint> wrap(Future<org.apache.hc.core5.reactor.ListenerEndpoint> future) {
        return new Future<ListenerEndpoint>() {
            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                return future.cancel(mayInterruptIfRunning);
            }

            @Override
            public boolean isCancelled() {
                return future.isCancelled();
            }

            @Override
            public boolean isDone() {
                return future.isDone();
            }

            @Override
            public ListenerEndpoint get() throws ExecutionException, InterruptedException {
                return new ListenerEndpointWrapper(future.get());
            }

            @Override
            public ListenerEndpoint get(long timeout, TimeUnit unit) throws ExecutionException, InterruptedException, TimeoutException {
                return new ListenerEndpointWrapper(future.get(timeout, unit));
            }
        };
    }

    public static FutureCallback<org.apache.hc.core5.reactor.ListenerEndpoint> wrap(FutureCallback<ListenerEndpoint> futureCallback) {
        return new FutureCallback<org.apache.hc.core5.reactor.ListenerEndpoint>() {
            @Override
            public void completed(org.apache.hc.core5.reactor.ListenerEndpoint result) {
                futureCallback.completed(new ListenerEndpointWrapper(result));
            }

            @Override
            public void failed(Exception ex) {
                futureCallback.failed(ex);
            }

            @Override
            public void cancelled() {
                futureCallback.cancelled();
            }
        };
    }

    @Override
    public SocketAddress getAddress() {
        return listenerEndpoint.getAddress();
    }

    @Override
    public boolean isClosed() {
        return listenerEndpoint.isClosed();
    }

    @Override
    public void close(boolean immediate) {
        listenerEndpoint.close(immediate ? CloseMode.IMMEDIATE : CloseMode.GRACEFUL);
    }

    @Override
    public void close() throws IOException {
        listenerEndpoint.close();
    }
}
