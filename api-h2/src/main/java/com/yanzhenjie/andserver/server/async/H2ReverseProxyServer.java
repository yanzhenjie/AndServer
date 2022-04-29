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
package com.yanzhenjie.andserver.server.async;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yanzhenjie.andserver.AndServer;
import com.yanzhenjie.andserver.handler.async.ProxyIncomingExchangeHandler;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.hc.core5.function.Callback;
import org.apache.hc.core5.function.Supplier;
import org.apache.hc.core5.http.EntityDetails;
import org.apache.hc.core5.http.HttpConnection;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.impl.Http1StreamListener;
import org.apache.hc.core5.http.impl.bootstrap.AsyncRequesterBootstrap;
import org.apache.hc.core5.http.impl.bootstrap.HttpAsyncRequester;
import org.apache.hc.core5.http.impl.nio.BufferedData;
import org.apache.hc.core5.http.nio.AsyncClientEndpoint;
import org.apache.hc.core5.http.nio.AsyncServerExchangeHandler;
import org.apache.hc.core5.http.nio.CapacityChannel;
import org.apache.hc.core5.http.nio.DataStreamChannel;
import org.apache.hc.core5.http.nio.ResponseChannel;
import org.apache.hc.core5.io.CloseMode;
import org.apache.hc.core5.pool.ConnPoolListener;
import org.apache.hc.core5.pool.ConnPoolStats;
import org.apache.hc.core5.pool.PoolStats;

import java.io.IOException;
import java.net.SocketException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Created by ISNing on 2022/4/28.
 */
public class H2ReverseProxyServer extends BasicH2Server<H2ReverseProxyServer.Builder> {

    public static final String SUB_TAG = "H2ReverseProxyServer";
    public static final String TAG = AndServer.genAndServerTag(SUB_TAG);

    private static final int INIT_BUFFER_SIZE = 4096;
    private final Map<String, HttpHost> mHostList;
    private HttpAsyncRequester mRequester;

    private H2ReverseProxyServer(Builder builder) {
        super(builder);

        this.mHostList = builder.mHostList;
    }

    public static H2ReverseProxyServer.Builder newBuilder() {
        return new H2ReverseProxyServer.Builder();
    }

    @NonNull
    @Override
    protected Collection<ImmutableTriple<String, String, Supplier<AsyncServerExchangeHandler>>> requestHandlers() {
        Set<ImmutableTriple<String, String, Supplier<AsyncServerExchangeHandler>>> tripleSet = new HashSet<>(mHostList.size());
        if (mRequester == null) {
            initializeRequester();
        }
        for (String hostname : mHostList.keySet()) {
            ImmutableTriple<String, String, Supplier<AsyncServerExchangeHandler>> triple =
                    new ImmutableTriple<>(hostname, "*",
                            () -> new ProxyIncomingExchangeHandler(
                                    mHostList.get(hostname), mRequester, INIT_BUFFER_SIZE));
            tripleSet.add(triple);
        }
        return tripleSet;
    }

    @NonNull
    @Override
    protected Http1StreamListener requestHttp1StreamListener() {
        return new Http1StreamListener() {
            @Override
            public void onRequestHead(final HttpConnection connection, final HttpRequest request) {
                Log.d(TAG, "[client->proxy] " + Thread.currentThread() + " " +
                        request.getMethod() + " " + request.getRequestUri());
            }

            @Override
            public void onResponseHead(final HttpConnection connection, final HttpResponse response) {
                Log.d(TAG, "[client<-proxy] " + Thread.currentThread() + " status " + response.getCode());
            }

            @Override
            public void onExchangeComplete(final HttpConnection connection, final boolean keepAlive) {
                Log.d(TAG, "[client<-proxy] " + Thread.currentThread() + " exchange completed; " +
                        "connection " + (keepAlive ? "kept alive" : "cannot be kept alive"));
            }

        };
    }

    @NonNull
    @Override
    protected Callback<Exception> requestExceptionCallback() {
        return object -> {
            if (object instanceof SocketException) {
                Log.e(TAG, "[client->proxy] " + Thread.currentThread() + " " + object.getMessage());
            } else {
                Log.e(TAG, "[client->proxy] " + Thread.currentThread() + " " + object.getMessage());
                object.printStackTrace();
            }
        };
    }

    @Nullable
    @Override
    protected Runnable requestStartupHook() {
        return this::initializeRequester;
    }

    @Nullable
    @Override
    protected Runnable requestShutdownHook() {
        return () -> mRequester.close(CloseMode.GRACEFUL);
    }

    private void initializeRequester() {
        mRequester = AsyncRequesterBootstrap.bootstrap()
                .setIOReactorConfig(this.getIOReactorConfig() == null ?
                        null : this.getIOReactorConfig().getIOReactorConfig())
                .setStreamListener(new Http1StreamListener() {
                    @Override
                    public void onRequestHead(final HttpConnection connection, final HttpRequest request) {
                        Log.d(TAG, "[proxy->origin] " + Thread.currentThread() + " " +
                                request.getMethod() + " " + request.getRequestUri());
                    }

                    @Override
                    public void onResponseHead(final HttpConnection connection, final HttpResponse response) {
                        Log.d(TAG, "[proxy<-origin] " + Thread.currentThread() + " status " + response.getCode());
                    }

                    @Override
                    public void onExchangeComplete(final HttpConnection connection, final boolean keepAlive) {
                        Log.d(TAG, "[proxy<-origin] " + Thread.currentThread() + " exchange completed; " +
                                "connection " + (keepAlive ? "kept alive" : "cannot be kept alive"));
                    }
                })
                .setConnPoolListener(new ConnPoolListener<HttpHost>() {
                    @Override
                    public void onLease(final HttpHost route, final ConnPoolStats<HttpHost> connPoolStats) {
                        Log.d(TAG, "[proxy->origin] " + Thread.currentThread() + " connection leased " + route);
                    }

                    @Override
                    public void onRelease(final HttpHost route, final ConnPoolStats<HttpHost> connPoolStats) {
                        final StringBuilder buf = new StringBuilder();
                        buf.append("[proxy->origin] ").append(Thread.currentThread()).append(" connection released ").append(route);
                        final PoolStats totals = connPoolStats.getTotalStats();
                        buf.append("; total kept alive: ").append(totals.getAvailable()).append("; ");
                        buf.append("total allocated: ").append(totals.getLeased() + totals.getAvailable());
                        buf.append(" of ").append(totals.getMax());
                        Log.d(TAG, buf.toString());
                    }
                })
                .create();

        mRequester.start();
    }

    public static class Builder extends BasicH2Server.Builder<Builder, H2ReverseProxyServer>
            implements H2ReverseProxyServer.ProxyBuilder<Builder, H2ReverseProxyServer> {

        private final Map<String, HttpHost> mHostList = new HashMap<>();

        public Builder() {
        }

        @Override
        public Builder addProxy(String hostName, String proxyHost) throws URISyntaxException {
            mHostList.put(StringUtils.lowerCase(hostName), HttpHost.create(proxyHost));
            return this;
        }

        @Override
        public H2ReverseProxyServer build() {
            return new H2ReverseProxyServer(this);
        }
    }

    public static class ProxyBuffer extends BufferedData {

        public ProxyBuffer(final int bufferSize) {
            super(bufferSize);
        }

        public int write(final DataStreamChannel channel) throws IOException {
            setOutputMode();
            if (buffer().hasRemaining()) {
                return channel.write(buffer());
            }
            return 0;
        }
    }

    public static class ProxyExchangeState {

        public final String id;

        public HttpRequest request;
        public EntityDetails requestEntityDetails;
        public DataStreamChannel requestDataChannel;
        public CapacityChannel requestCapacityChannel;
        public ProxyBuffer inBuf;
        public boolean inputEnd;

        public HttpResponse response;
        public EntityDetails responseEntityDetails;
        public ResponseChannel responseMessageChannel;
        public DataStreamChannel responseDataChannel;
        public CapacityChannel responseCapacityChannel;
        public ProxyBuffer outBuf;
        public boolean outputEnd;

        public AsyncClientEndpoint clientEndpoint;

        public ProxyExchangeState() {
            this.id = UUID.randomUUID().toString();
        }
    }
}
