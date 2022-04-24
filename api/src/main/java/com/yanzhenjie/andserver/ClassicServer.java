/*
 * Copyright (C) 2018 Zhenjie Yan
 *               2022 ISNing
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
import com.yanzhenjie.andserver.delegate.ExceptionListener;
import com.yanzhenjie.andserver.delegate.SocketConfigDelegate;

import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpResponseFactory;
import org.apache.hc.core5.http.impl.io.DefaultBHttpServerConnection;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;

/**
 * Created by Zhenjie Yan on 2018/9/10.
 */
public interface ClassicServer extends Server {

    /**
     * Get the local address of this server socket.
     *
     * @return {@link InetAddress}.
     * @throws IllegalStateException if the server is not started, an IllegalStateException is thrown.
     * @see ServerSocket#getInetAddress()
     */
    InetAddress getLocalAddress();

    /**
     * Returns the port number on which this socket is listening.
     *
     * @return the local port number to which this socket is bound or -1 if the socket is not bound yet.
     * @throws IllegalStateException if the server is not started, an IllegalStateException is thrown.
     * @see Socket#getLocalPort()
     */
    int getLocalPort();

    interface Builder<T extends Builder<T, S>, S extends ClassicServer>
            extends Server.Builder<T, S, ServerListener> {
        /**
         * Specify the port on which the server listens.
         */
        T setListenerPort(int listenerPort);

        /**
         * Specify local address.
         */
        T setLocalAddress(InetAddress localAddress);

        /**
         * Assigns {@link SocketConfigDelegate} instance.
         */
        T setSocketConfig(SocketConfigDelegate socketConfig);

        /**
         * Assigns {@link HttpResponseFactory<ClassicHttpResponse>} instance.
         */
        @RestrictTo(RestrictTo.Scope.LIBRARY)
        T setResponseFactory(HttpResponseFactory<ClassicHttpResponse> responseFactory);

        /**
         * Assigns {@link ServerSocketFactory} instance.
         */
        T setServerSocketFactory(ServerSocketFactory serverSocketFactory);

        /**
         * Assigns {@link SSLContext} instance.
         */
        T setSslContext(SSLContext sslContext);

        /**
         * Assigns {@link Callback<SSLParameters>} instance.
         * Will be called after server class defined callback.
         */
        T setSslSetupHandler(Callback<SSLParameters> sslSetupHandler);

        /**
         * Assigns {@link org.apache.hc.core5.http.io.HttpConnectionFactory} instance.
         */
        @RestrictTo(RestrictTo.Scope.LIBRARY)
        T setConnectionFactory(org.apache.hc.core5.http.io.HttpConnectionFactory<? extends DefaultBHttpServerConnection> connectionFactory);

        /**
         * Assigns {@link ExceptionListener} instance.
         * Will be called after server class defined listener.
         */
        T setExceptionListener(ExceptionListener exceptionListener);
    }

    interface ProxyBuilder<T extends ProxyBuilder<T, S>, S extends ClassicServer>
            extends Server.Builder<T, S, ServerListener>, Server.ProxyBuilder<T, S, ServerListener> {
    }

    interface ServerListener extends Server.ServerListener {
    }
}