/*
 * Copyright 2020 Zhenjie Yan.
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
package com.yanzhenjie.andserver.server;

import androidx.annotation.NonNull;

import com.yanzhenjie.andserver.AndServer;
import com.yanzhenjie.andserver.ProxyHandler;
import com.yanzhenjie.andserver.SSLSocketInitializer;
import com.yanzhenjie.andserver.Server;
import com.yanzhenjie.andserver.util.Executors;

import org.apache.httpcore.ConnectionClosedException;
import org.apache.httpcore.HttpException;
import org.apache.httpcore.HttpHost;
import org.apache.httpcore.HttpServerConnection;
import org.apache.httpcore.impl.DefaultBHttpClientConnection;
import org.apache.httpcore.impl.DefaultBHttpServerConnection;
import org.apache.httpcore.protocol.BasicHttpContext;
import org.apache.httpcore.protocol.HttpCoreContext;
import org.apache.httpcore.protocol.HttpProcessor;
import org.apache.httpcore.protocol.HttpRequestHandler;
import org.apache.httpcore.protocol.HttpService;
import org.apache.httpcore.protocol.ImmutableHttpProcessor;
import org.apache.httpcore.protocol.ResponseConnControl;
import org.apache.httpcore.protocol.ResponseContent;
import org.apache.httpcore.protocol.ResponseDate;
import org.apache.httpcore.protocol.ResponseServer;
import org.apache.httpcore.protocol.UriHttpRequestHandlerMapper;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;

/**
 * Created by Zhenjie Yan on 3/7/20.
 */
public class ProxyServer extends BasicServer<ProxyServer.Builder> {

    public static final String PROXY_CONN_CLIENT = "http.proxy.conn.client";
    public static final String PROXY_CONN_ALIVE = "http.proxy.conn.alive";

    public static ProxyServer.Builder newBuilder() {
        return new ProxyServer.Builder();
    }

    private final InetAddress mInetAddress;
    private final int mPort;
    private final int mTimeout;
    private final ServerSocketFactory mSocketFactory;
    private final SSLContext mSSLContext;
    private final SSLSocketInitializer mSSLSocketInitializer;
    private final Server.ServerListener mListener;

    private Map<String, HttpHost> mHostList;

    private HttpServer mHttpServer;
    private boolean isRunning;

    private ProxyServer(Builder builder) {
        super(builder);
        this.mInetAddress = builder.inetAddress;
        this.mPort = builder.port;
        this.mTimeout = builder.timeout;
        this.mSocketFactory = builder.mSocketFactory;
        this.mSSLContext = builder.sslContext;
        this.mSSLSocketInitializer = builder.mSSLSocketInitializer;
        this.mListener = builder.listener;

        this.mHostList = builder.mHostList;
    }

    @Override
    protected HttpRequestHandler requestHandler() {
        return new ProxyHandler(mHostList);
    }

    @Override
    public void startup() {
        if (isRunning) {
            return;
        }

        Executors.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                ServerSocketFactory socketFactory = mSocketFactory;
                if (socketFactory == null) {
                    if (mSSLContext != null) {
                        socketFactory = mSSLContext.getServerSocketFactory();
                    } else {
                        socketFactory = ServerSocketFactory.getDefault();
                    }
                }

                mHttpServer = new HttpServer(mInetAddress,
                    mPort,
                    mTimeout,
                    socketFactory,
                    mSSLSocketInitializer,
                    requestHandler());
                try {
                    mHttpServer.startServer();
                    isRunning = true;

                    Executors.getInstance().post(new Runnable() {
                        @Override
                        public void run() {
                            if (mListener != null) {
                                mListener.onStarted();
                            }
                        }
                    });
                    Runtime.getRuntime().addShutdownHook(new Thread() {
                        @Override
                        public void run() {
                            mHttpServer.stopServer();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void shutdown() {
        if (!isRunning) {
            return;
        }

        Executors.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                if (mHttpServer != null) {
                    mHttpServer.stopServer();
                    isRunning = false;
                    Executors.getInstance().post(new Runnable() {
                        @Override
                        public void run() {
                            if (mListener != null) {
                                mListener.onStopped();
                            }
                        }
                    });
                }
            }
        });
    }

    public static class Builder extends BasicServer.Builder<Builder, ProxyServer>
        implements Server.ProxyBuilder<Builder, ProxyServer> {

        private Map<String, HttpHost> mHostList = new HashMap<>();

        public Builder() {
        }

        @Override
        public Builder addProxy(String hostName, String proxyHost) {
            mHostList.put(hostName.toLowerCase(Locale.ROOT), HttpHost.create(proxyHost));
            return this;
        }

        @Override
        public ProxyServer build() {
            return new ProxyServer(this);
        }
    }

    private static class HttpServer implements Runnable {

        private final InetAddress mInetAddress;
        private final int mPort;
        private final int mTimeout;
        private final ServerSocketFactory mSocketFactory;
        private final SSLSocketInitializer mSSLSocketInitializer;
        private final HttpRequestHandler mHandler;

        private final ThreadPoolExecutor mServerExecutor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
            new SynchronousQueue<>(), new ThreadFactoryImpl("HTTP-Server-"));
        private final ThreadGroup mWorkerThreads = new ThreadGroup("HTTP-workers");
        private final ThreadPoolExecutor mWorkerExecutor = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 1L,
            TimeUnit.SECONDS, new SynchronousQueue<>(), new ThreadFactoryImpl("HTTP-Handlers-", mWorkerThreads)) {
            @Override
            protected void beforeExecute(Thread t, Runnable r) {
                if (r instanceof Worker) {
                    mWorkerSet.put((Worker) r, Boolean.TRUE);
                }
            }

            @Override
            protected void afterExecute(Runnable r, Throwable t) {
                if (r instanceof Worker) {
                    mWorkerSet.remove(r);
                }
            }
        };
        private final Map<Worker, Boolean> mWorkerSet = new ConcurrentHashMap<>();

        private HttpService mHttpService;
        private ServerSocket mServerSocket;

        public HttpServer(InetAddress inetAddress, int port, int timeout, ServerSocketFactory socketFactory,
            SSLSocketInitializer sslSocketInitializer, HttpRequestHandler handler) {
            this.mInetAddress = inetAddress;
            this.mPort = port;
            this.mTimeout = timeout;
            this.mSocketFactory = socketFactory;
            this.mSSLSocketInitializer = sslSocketInitializer;
            this.mHandler = handler;

            HttpProcessor inProcessor = new ImmutableHttpProcessor(
                new ResponseDate(),
                new ResponseServer(AndServer.INFO),
                new ResponseContent(),
                new ResponseConnControl());

            UriHttpRequestHandlerMapper mapper = new UriHttpRequestHandlerMapper();
            mapper.register("*", mHandler);

            this.mHttpService = new HttpService(inProcessor, mapper);
        }

        public void startServer() throws IOException {
            mServerSocket = mSocketFactory.createServerSocket();
            mServerSocket.setReuseAddress(true);
            mServerSocket.bind(new InetSocketAddress(mInetAddress, mPort), BUFFER);
            mServerSocket.setReceiveBufferSize(BUFFER);
            if (mSSLSocketInitializer != null && mServerSocket instanceof SSLServerSocket) {
                mSSLSocketInitializer.onCreated((SSLServerSocket) mServerSocket);
            }

            mServerExecutor.execute(this);
        }

        public void stopServer() {
            mServerExecutor.shutdown();
            mWorkerExecutor.shutdown();
            try {
                mServerSocket.close();
            } catch (IOException ignored) {
            }
            mWorkerThreads.interrupt();

            try {
                mWorkerExecutor.awaitTermination(3, TimeUnit.SECONDS);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }

            Set<Worker> workers = mWorkerSet.keySet();
            for (Worker worker : workers) {
                HttpServerConnection conn = worker.getServerConn();
                try {
                    conn.shutdown();
                } catch (IOException ignored) {
                }
            }
        }

        @Override
        public void run() {
            try {
                while (!Thread.interrupted()) {
                    Socket socket = mServerSocket.accept();
                    socket.setSoTimeout(mTimeout);
                    socket.setKeepAlive(true);
                    socket.setTcpNoDelay(true);
                    socket.setReceiveBufferSize(BUFFER);
                    socket.setSendBufferSize(BUFFER);
                    socket.setSoLinger(true, 0);

                    DefaultBHttpServerConnection serverConn = new DefaultBHttpServerConnection(BUFFER);
                    serverConn.bind(socket);

                    DefaultBHttpClientConnection clientConn = new DefaultBHttpClientConnection(BUFFER);
                    Worker worker = new Worker(mHttpService, serverConn, clientConn);

                    mWorkerExecutor.execute(worker);
                }
            } catch (Exception ignored) {
            }
        }
    }

    private static class Worker implements Runnable {

        private final HttpService mHttpService;
        private final DefaultBHttpServerConnection mServerConn;
        private final DefaultBHttpClientConnection mClientConn;

        public Worker(HttpService httpservice,
            DefaultBHttpServerConnection serverConn, DefaultBHttpClientConnection clientConn) {
            this.mHttpService = httpservice;
            this.mServerConn = serverConn;
            this.mClientConn = clientConn;
        }

        public DefaultBHttpServerConnection getServerConn() {
            return mServerConn;
        }

        @Override
        public void run() {
            BasicHttpContext localContext = new BasicHttpContext();
            HttpCoreContext context = HttpCoreContext.adapt(localContext);
            context.setAttribute(PROXY_CONN_CLIENT, mClientConn);

            try {
                while (!Thread.interrupted()) {
                    if (!mServerConn.isOpen()) {
                        mClientConn.close();
                        break;
                    }

                    mHttpService.handleRequest(mServerConn, context);

                    Boolean keepAlive = (Boolean) context.getAttribute(PROXY_CONN_ALIVE);
                    if (!Boolean.TRUE.equals(keepAlive)) {
                        mClientConn.close();
                        mServerConn.close();
                        break;
                    }
                }
            } catch (ConnectionClosedException ex) {
                System.err.println("Client closed connection.");
            } catch (IOException ex) {
                System.err.println("I/O error: " + ex.getMessage());
            } catch (HttpException ex) {
                System.err.println("Unrecoverable HTTP protocol violation: " + ex.getMessage());
            } finally {
                try {
                    mServerConn.shutdown();
                } catch (IOException ignore) {
                }
                try {
                    mClientConn.shutdown();
                } catch (IOException ignore) {
                }
            }
        }
    }

    private static class ThreadFactoryImpl implements ThreadFactory {
        private final String mPrefix;
        private final ThreadGroup mGroup;
        private final AtomicLong mCount;

        ThreadFactoryImpl(String prefix, ThreadGroup group) {
            this.mPrefix = prefix;
            this.mGroup = group;
            this.mCount = new AtomicLong();
        }

        ThreadFactoryImpl(String mPrefix) {
            this(mPrefix, null);
        }

        @Override
        public Thread newThread(@NonNull Runnable target) {
            return new Thread(mGroup, target, mPrefix + "-" + mCount.incrementAndGet());
        }
    }
}