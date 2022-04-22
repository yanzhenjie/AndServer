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

import android.util.Log;

import androidx.annotation.NonNull;

import com.yanzhenjie.andserver.AndServer;
import com.yanzhenjie.andserver.ProxyHandler;
import com.yanzhenjie.andserver.Server;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.hc.core5.http.ConnectionClosedException;
import org.apache.hc.core5.http.ExceptionListener;
import org.apache.hc.core5.http.HttpConnection;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.impl.Http1StreamListener;
import org.apache.hc.core5.http.impl.bootstrap.HttpRequester;
import org.apache.hc.core5.http.impl.bootstrap.RequesterBootstrap;
import org.apache.hc.core5.http.io.HttpRequestHandler;
import org.apache.hc.core5.pool.ConnPoolListener;
import org.apache.hc.core5.pool.ConnPoolStats;
import org.apache.hc.core5.pool.PoolStats;

import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Zhenjie Yan on 3/7/20.
 */
public class ProxyServer extends BasicServer<ProxyServer.Builder> {

    public static final String SUB_TAG = "ProxyServer";
    public static final String TAG = AndServer.genAndServerTag(SUB_TAG);

    public static ProxyServer.Builder newBuilder() {
        return new ProxyServer.Builder();
    }

    private final Map<String, HttpHost> mHostList;

    private HttpRequester mRequester;

    private ProxyServer(Builder builder) {
        super(builder);

        this.mHostList = builder.mHostList;
    }

    @NonNull
    @Override
    protected Collection<ImmutableTriple<String, String, HttpRequestHandler>> requestHandlers() {
        Set<ImmutableTriple<String, String, HttpRequestHandler>> tripleSet = new HashSet<>(mHostList.size());
        if (mRequester == null) {
            initializeRequester();
        }
        for (String hostname : mHostList.keySet()) {
            ImmutableTriple<String, String, HttpRequestHandler> triple = new ImmutableTriple<>(hostname, "*", new ProxyHandler(mHostList.get(hostname), mRequester));
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
    protected ExceptionListener requestExceptionListener() {
        return new ExceptionListener() {

            @Override
            public void onError(final Exception ex) {
                if (ex instanceof SocketException) {
                    Log.d(TAG, "[client->proxy] " + Thread.currentThread() + " " + ex.getMessage());
                } else {
                    Log.d(TAG, "[client->proxy] " + Thread.currentThread()  + " " + ex.getMessage());
                    ex.printStackTrace();
                }
            }

            @Override
            public void onError(final HttpConnection connection, final Exception ex) {
                if (ex instanceof SocketTimeoutException) {
                    Log.d(TAG, "[client->proxy] " + Thread.currentThread() + " time out");
                } else if (ex instanceof SocketException || ex instanceof ConnectionClosedException) {
                    Log.d(TAG, "[client->proxy] " + Thread.currentThread() + " " + ex.getMessage());
                } else {
                    Log.d(TAG, "[client->proxy] " + Thread.currentThread() + " " + ex.getMessage());
                    ex.printStackTrace();
                }
            }

        };
    }

    private HttpRequester initializeRequester() {
        mRequester = RequesterBootstrap.bootstrap()
                .setStreamListener(new Http1StreamListener() {

                    @Override
                    public void onRequestHead(final HttpConnection connection, final HttpRequest request) {
                        Log.d(TAG, "[proxy->origin] " + Thread.currentThread()  + " " +
                                request.getMethod() + " " + request.getRequestUri());
                    }

                    @Override
                    public void onResponseHead(final HttpConnection connection, final HttpResponse response) {
                        Log.d(TAG, "[proxy<-origin] " + Thread.currentThread()  + " status " + response.getCode());
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
        return mRequester;
    }

    public static class Builder extends BasicServer.Builder<Builder, ProxyServer>
        implements Server.ProxyBuilder<Builder, ProxyServer> {

        private final Map<String, HttpHost> mHostList = new HashMap<>();

        public Builder() {
        }

        @Override
        public Builder addProxy(String hostName, String proxyHost) throws URISyntaxException {
            mHostList.put(StringUtils.lowerCase(hostName), HttpHost.create(proxyHost));
            return this;
        }

        @Override
        public ProxyServer build() {
            return new ProxyServer(this);
        }
    }
}