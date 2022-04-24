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
package com.yanzhenjie.andserver;

import androidx.annotation.RestrictTo;

import com.yanzhenjie.andserver.delegate.Callback;
import com.yanzhenjie.andserver.delegate.FutureCallback;
import com.yanzhenjie.andserver.delegate.IOReactorConfigDelegate;
import com.yanzhenjie.andserver.delegate.ListenerEndpoint;
import com.yanzhenjie.andserver.delegate.ssl.BasicClientTlsStrategy;
import com.yanzhenjie.andserver.delegate.ssl.BasicServerTlsStrategy;
import com.yanzhenjie.andserver.delegate.ssl.TlsStrategy;
import com.yanzhenjie.andserver.http.URIScheme;

import org.apache.hc.core5.function.Decorator;
import org.apache.hc.core5.reactor.IOSession;
import org.apache.hc.core5.reactor.IOSessionListener;

import java.net.SocketAddress;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by ISNing on 2022/4/23.
 */
public interface AsyncServer extends Server {

    /**
     * Listen on on address.
     */
    Future<ListenerEndpoint> listen(SocketAddress address, URIScheme scheme, Object attachment, FutureCallback<ListenerEndpoint> callback);

    /**
     * Listen on on address.
     */
    Future<ListenerEndpoint> listen(SocketAddress address, URIScheme scheme, FutureCallback<ListenerEndpoint> callback);

    /**
     * Listen on on address.
     */
    Future<ListenerEndpoint> listen(SocketAddress address, URIScheme scheme);

    /**
     * Listen on on address.
     */
    Future<ListenerEndpoint> listen(SocketAddress address, Object attachment, FutureCallback<ListenerEndpoint> callback);

    /**
     * Get all endpoints listening on.
     */
    Set<ListenerEndpoint> getEndPoints();

    interface Builder<T extends Builder<T, S>, S extends AsyncServer> extends Server.Builder<T, S, ServerListener> {
        /**
         * Assigns {@link IOReactorConfigDelegate} instance.
         */
        T setIOReactorConfig(IOReactorConfigDelegate ioReactorConfig);

        /**
         * Assigns {@link TlsStrategy} instance.
         *
         * @see BasicClientTlsStrategy
         * @see BasicServerTlsStrategy
         */
        T setTlsStrategy(TlsStrategy tlsStrategy);

        /**
         * Set TLS handshaking timeout.
         */
        T setTlsHandshakeTimeout(long duration, TimeUnit timeUnit);

        /**
         * Assigns {@link Decorator<IOSession>} instance.
         * Will be called after server class defined decorator.
         */
        @RestrictTo(RestrictTo.Scope.LIBRARY)
        T setIOSessionDecorator(Decorator<IOSession> ioSessionDecorator);

        /**
         * Assigns {@link Callback<Exception>} instance.
         * Will be called after server class defined callback.
         */
        T setExceptionCallback(Callback<Exception> exceptionCallback);

        /**
         * Assigns {@link IOSessionListener} instance.
         * Will be called after server class defined listener.
         */
        @RestrictTo(RestrictTo.Scope.LIBRARY)
        T setIOSessionListener(IOSessionListener sessionListener);
    }

    interface ProxyBuilder<T extends ProxyBuilder<T, S>, S extends AsyncServer>
            extends Builder<T, S>, Server.ProxyBuilder<T, S, ServerListener> {
    }

    interface ServerListener extends Server.ServerListener {
    }
}