/*
 * Copyright © Yan Zhenjie. All Rights Reserved
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

import com.yanzhenjie.andserver.exception.resolver.ExceptionResolver;
import com.yanzhenjie.andserver.interceptor.Interceptor;
import com.yanzhenjie.andserver.ssl.SSLSocketInitializer;
import com.yanzhenjie.andserver.website.WebSite;

import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;

/**
 * <p>The control of the server.</p>
 * Created by Yan Zhenjie on 2016/6/13.
 */
public interface Server {

    /**
     * Start server.
     */
    void start();

    /**
     * Get the network address.
     */
    InetAddress getInetAddress();

    /**
     * Stop server.
     */
    void stop();

    /**
     * Is the server running?
     *
     * @return return true, not return false.
     */
    boolean isRunning();

    interface Builder {

        Builder inetAddress(InetAddress inetAddress);

        Builder port(int port);

        Builder timeout(int timeout, TimeUnit timeUnit);

        Builder sslContext(SSLContext sslContext);

        Builder sslSocketInitializer(SSLSocketInitializer initializer);

        Builder interceptor(Interceptor interceptor);

        Builder exceptionResolver(ExceptionResolver resolver);

        Builder registerHandler(String path, RequestHandler handler);

        Builder website(WebSite webSite);

        Builder listener(Listener listener);

        Server build();
    }

    interface Listener extends Core.StartupListener {
    }
}
