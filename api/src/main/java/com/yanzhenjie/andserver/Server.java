/*
 * Copyright © 2018 Zhenjie Yan.
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

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLContext;

/**
 * Created by Zhenjie Yan on 2018/9/10.
 */
public interface Server {

    /**
     * Server running status.
     *
     * @return return true, not return false.
     */
    boolean isRunning();

    /**
     * Start the server.
     */
    void startup();

    /**
     * Quit the server.
     */
    void shutdown();

    /**
     * Get the local address of this server socket.
     *
     * @return {@link InetAddress}.
     *
     * @throws IllegalStateException if the server is not started, an IllegalStateException is thrown.
     * @see ServerSocket#getInetAddress()
     */
    InetAddress getInetAddress();

    /**
     * Returns the port number on which this socket is listening.
     *
     * @return the local port number to which this socket is bound or -1 if the socket is not bound yet.
     *
     * @throws IllegalStateException if the server is not started, an IllegalStateException is thrown.
     * @see Socket#getLocalPort()
     */
    int getPort();

    interface Builder<T extends Builder, S extends Server> {

        /**
         * Specified server need to monitor the ip address.
         */
        T inetAddress(InetAddress inetAddress);

        /**
         * Specify the port on which the server listens.
         */
        T port(int port);

        /**
         * Connection and response timeout.
         */
        T timeout(int timeout, TimeUnit timeUnit);

        /**
         * Assigns {@link ServerSocketFactory} instance.
         */
        T serverSocketFactory(ServerSocketFactory factory);

        /**
         * Assigns {@link SSLContext} instance.
         */
        T sslContext(SSLContext sslContext);

        /**
         * Assigns {@link SSLSocketInitializer} instance.
         */
        T sslSocketInitializer(SSLSocketInitializer initializer);

        /**
         * Set the server listener.
         */
        T listener(Server.ServerListener listener);

        /**
         * Create a server.
         */
        S build();
    }

    interface ProxyBuilder<T extends ProxyBuilder, S extends Server> {

        /**
         * Add host address to proxy.
         *
         * @param hostName such as: {@code www.example.com}, {@code api.example.com}, {@code 192.168.1.111}.
         * @param proxyHost such as: {@code http://127.0.0.1:8080}, {@code http://localhost:8181}
         */
        T addProxy(String hostName, String proxyHost);

        /**
         * Specified server need to monitor the ip address.
         */
        T inetAddress(InetAddress inetAddress);

        /**
         * Specify the port on which the server listens.
         */
        T port(int port);

        /**
         * Connection and response timeout.
         */
        T timeout(int timeout, TimeUnit timeUnit);

        /**
         * Assigns {@link ServerSocketFactory} instance.
         */
        T serverSocketFactory(ServerSocketFactory factory);

        /**
         * Assigns {@link SSLContext} instance.
         */
        T sslContext(SSLContext sslContext);

        /**
         * Assigns {@link SSLSocketInitializer} instance.
         */
        T sslSocketInitializer(SSLSocketInitializer initializer);

        /**
         * Set the server listener.
         */
        T listener(Server.ServerListener listener);

        /**
         * Create a server.
         */
        S build();
    }

    interface ServerListener {

        /**
         * When the server is started.
         */
        void onStarted();

        /**
         * When the server stops running.
         */
        void onStopped();

        /**
         * An error occurred while starting the server.
         */
        void onException(Exception e);
    }
}