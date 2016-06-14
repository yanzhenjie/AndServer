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

import com.yanzhenjie.andserver.util.AndWebUtil;

import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.impl.DefaultHttpServerConnection;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpProcessor;
import org.apache.http.protocol.HttpRequestHandlerRegistry;
import org.apache.http.protocol.HttpService;
import org.apache.http.protocol.ResponseConnControl;
import org.apache.http.protocol.ResponseContent;
import org.apache.http.protocol.ResponseDate;
import org.apache.http.protocol.ResponseServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

/**
 * <p>AndServer Core. Mainly is to establish the service side, distribute the requests.</p>
 * Created on 2016/6/13.
 *
 * @author Yan Zhenjie.
 */
class DefaultAndServer extends Thread implements AndServer {
    /**
     * Server socket.
     */
    private ServerSocket mServerSocket;
    /**
     * Bind port.
     */
    private int mPort;
    /**
     * Connection timeout.
     */
    private int timeout;
    /**
     * The api list.
     */
    private Map<String, AndServerRequestHandler> mRequestHandlerMap;

    /**
     * 是否循环接受客户端请求。
     **/
    private boolean isLoop = true;

    /**
     * To construct the service.
     *
     * @param port              port code.
     * @param requestHandlerMap api list.
     */
    DefaultAndServer(int port, int timeout, Map<String, AndServerRequestHandler> requestHandlerMap) {
        this.mPort = port;
        this.timeout = timeout;
        this.mRequestHandlerMap = requestHandlerMap;
    }

    @Override
    public void run() {
        try {
            mServerSocket = new ServerSocket();
            mServerSocket.setReuseAddress(true);
            mServerSocket.bind(new InetSocketAddress(mPort));

            // HTTP协议拦截器。
            BasicHttpProcessor httpProcessor = new BasicHttpProcessor();
            httpProcessor.addInterceptor(new ResponseDate());
            httpProcessor.addInterceptor(new ResponseServer());
            httpProcessor.addInterceptor(new ResponseContent());
            httpProcessor.addInterceptor(new ResponseConnControl());

            // HTTP Attribute.
            HttpParams httpParams = new BasicHttpParams();
            httpParams.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, timeout)
                    .setIntParameter(CoreConnectionPNames.SOCKET_BUFFER_SIZE, 8 * 1024)
                    .setBooleanParameter(CoreConnectionPNames.STALE_CONNECTION_CHECK, false)
                    .setBooleanParameter(CoreConnectionPNames.TCP_NODELAY, true)
                    .setParameter(CoreProtocolPNames.ORIGIN_SERVER, "WebServer/1.1");

            // 注册Http接口。
            HttpRequestHandlerRegistry handlerRegistry = new HttpRequestHandlerRegistry();
            for (Map.Entry<String, AndServerRequestHandler> handlerEntry : mRequestHandlerMap.entrySet()) {
                handlerRegistry.register("/" + handlerEntry.getKey(), new DefaultHttpRequestHandler(handlerEntry.getValue()));
            }

            // 创建HTTP服务。
            HttpService httpService = new HttpService(httpProcessor, new DefaultConnectionReuseStrategy(), new DefaultHttpResponseFactory());
            httpService.setParams(httpParams);
            httpService.setHandlerResolver(handlerRegistry);

            /**
             * 开始接受客户端请求。
             */
            while (isLoop) {
                // 接收客户端套接字。
                if (!mServerSocket.isClosed()) {
                    Socket socket = mServerSocket.accept();
                    DefaultHttpServerConnection serverConnection = new DefaultHttpServerConnection();
                    serverConnection.bind(socket, httpParams);

                    // Dispatch request handler.
                    RequestHandleTask requestTask = new RequestHandleTask(this, httpService, serverConnection);
                    requestTask.setDaemon(true);
                    AndWebUtil.executeRunnable(requestTask);
                }
            }
        } catch (Exception e) {
        } finally {
            close();
        }
    }

    @Override
    public void launch() {
        start();
    }

    @Override
    public void close() {
        isLoop = false;
        try {
            if (mServerSocket != null) {
                mServerSocket.close();
                mServerSocket = null;
            }
        } catch (IOException e) {
        }
    }

    @Override
    public boolean isLooping() {
        return isLoop;
    }

    @Override
    public boolean isRunning() {
        return mServerSocket != null;
    }
}
