/*
 * Copyright (C) 2005-2020 The Apache Software Foundation
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
package com.yanzhenjie.andserver.handler.async;

import android.util.Log;

import com.yanzhenjie.andserver.AndServer;
import com.yanzhenjie.andserver.server.async.AsyncReverseProxyServer;

import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http.ConnectionClosedException;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.EntityDetails;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HeaderElements;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.impl.BasicEntityDetails;
import org.apache.hc.core5.http.impl.bootstrap.HttpAsyncRequester;
import org.apache.hc.core5.http.message.BasicHttpResponse;
import org.apache.hc.core5.http.nio.AsyncClientEndpoint;
import org.apache.hc.core5.http.nio.AsyncServerExchangeHandler;
import org.apache.hc.core5.http.nio.CapacityChannel;
import org.apache.hc.core5.http.nio.DataStreamChannel;
import org.apache.hc.core5.http.nio.ResponseChannel;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.http.protocol.HttpCoreContext;
import org.apache.hc.core5.util.Timeout;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ProxyIncomingExchangeHandler implements AsyncServerExchangeHandler {

    public static final String SUB_TAG = "ProxyIncomingExchangeHandler";
    public static final String TAG = AndServer.genAndServerTag(SUB_TAG);

    private final HttpHost targetHost;
    private final HttpAsyncRequester requester;
    private final AsyncReverseProxyServer.ProxyExchangeState exchangeState;

    private final int bufferSize;

    public ProxyIncomingExchangeHandler(final HttpHost targetHost, final HttpAsyncRequester requester, int bufferSize) {
        super();
        this.targetHost = targetHost;
        this.requester = requester;
        this.bufferSize = bufferSize;
        this.exchangeState = new AsyncReverseProxyServer.ProxyExchangeState();
    }

    @Override
    public void handleRequest(
            final HttpRequest incomingRequest,
            final EntityDetails entityDetails,
            final ResponseChannel responseChannel,
            final HttpContext httpContext) throws HttpException, IOException {

        synchronized (exchangeState) {
            Log.d(TAG, "[client->proxy] " + exchangeState.id + " " +
                    incomingRequest.getMethod() + " " + incomingRequest.getRequestUri());
            exchangeState.request = incomingRequest;
            exchangeState.requestEntityDetails = entityDetails;
            exchangeState.inputEnd = entityDetails == null;
            exchangeState.responseMessageChannel = responseChannel;

            if (entityDetails != null) {
                final Header h = incomingRequest.getFirstHeader(HttpHeaders.EXPECT);
                if (h != null && HeaderElements.CONTINUE.equalsIgnoreCase(h.getValue())) {
                    responseChannel.sendInformation(new BasicHttpResponse(HttpStatus.SC_CONTINUE), httpContext);
                }
            }
        }

        Log.d(TAG, "[proxy->origin] " + exchangeState.id + " request connection to " + targetHost);

        requester.connect(targetHost, Timeout.ofSeconds(30), null, new FutureCallback<AsyncClientEndpoint>() {

            @Override
            public void completed(final AsyncClientEndpoint clientEndpoint) {
                Log.d(TAG, "[proxy->origin] " + exchangeState.id + " connection leased");
                synchronized (exchangeState) {
                    exchangeState.clientEndpoint = clientEndpoint;
                }
                clientEndpoint.execute(
                        new ProxyOutgoingExchangeHandler(targetHost, clientEndpoint, exchangeState, bufferSize),
                        HttpCoreContext.create());
            }

            @Override
            public void failed(final Exception cause) {
                final HttpResponse outgoingResponse = new BasicHttpResponse(HttpStatus.SC_SERVICE_UNAVAILABLE);
                outgoingResponse.addHeader(HttpHeaders.CONNECTION, HeaderElements.CLOSE);
                final ByteBuffer msg = StandardCharsets.US_ASCII.encode(CharBuffer.wrap(cause.getMessage()));
                final EntityDetails exEntityDetails = new BasicEntityDetails(msg.remaining(),
                        ContentType.TEXT_PLAIN);
                synchronized (exchangeState) {
                    exchangeState.response = outgoingResponse;
                    exchangeState.responseEntityDetails = exEntityDetails;
                    exchangeState.outBuf = new AsyncReverseProxyServer.ProxyBuffer(1024);
                    exchangeState.outBuf.put(msg);
                    exchangeState.outputEnd = true;
                }
                Log.d(TAG, "[client<-proxy] " + exchangeState.id + " status " + outgoingResponse.getCode());

                try {
                    responseChannel.sendResponse(outgoingResponse, exEntityDetails, httpContext);
                } catch (final HttpException | IOException ignore) {
                    // ignore
                }
            }

            @Override
            public void cancelled() {
                failed(new InterruptedIOException());
            }
        });
    }

    @Override
    public void updateCapacity(final CapacityChannel capacityChannel) throws IOException {
        synchronized (exchangeState) {
            exchangeState.requestCapacityChannel = capacityChannel;
            final int capacity = exchangeState.inBuf != null ? exchangeState.inBuf.capacity() : bufferSize;
            if (capacity > 0) {
                Log.d(TAG, "[client<-proxy] " + exchangeState.id + " input capacity: " + capacity);
                capacityChannel.update(capacity);
            }
        }
    }

    @Override
    public void consume(final ByteBuffer src) throws IOException {
        synchronized (exchangeState) {
            Log.d(TAG, "[client->proxy] " + exchangeState.id + " " + src.remaining() + " bytes received");
            final DataStreamChannel dataChannel = exchangeState.requestDataChannel;
            if (dataChannel != null && exchangeState.inBuf != null) {
                if (exchangeState.inBuf.hasData()) {
                    final int bytesWritten = exchangeState.inBuf.write(dataChannel);
                    Log.d(TAG, "[proxy->origin] " + exchangeState.id + " " + bytesWritten + " bytes sent");
                }
                if (!exchangeState.inBuf.hasData()) {
                    final int bytesWritten = dataChannel.write(src);
                    Log.d(TAG, "[proxy->origin] " + exchangeState.id + " " + bytesWritten + " bytes sent");
                }
            }
            if (src.hasRemaining()) {
                if (exchangeState.inBuf == null) {
                    exchangeState.inBuf = new AsyncReverseProxyServer.ProxyBuffer(bufferSize);
                }
                exchangeState.inBuf.put(src);
            }
            final int capacity = exchangeState.inBuf != null ? exchangeState.inBuf.capacity() : bufferSize;
            Log.d(TAG, "[client<-proxy] " + exchangeState.id + " input capacity: " + capacity);
            if (dataChannel != null) {
                dataChannel.requestOutput();
            }
        }
    }

    @Override
    public void streamEnd(final List<? extends Header> trailers) throws HttpException, IOException {
        synchronized (exchangeState) {
            Log.d(TAG, "[client->proxy] " + exchangeState.id + " end of input");
            exchangeState.inputEnd = true;
            final DataStreamChannel dataChannel = exchangeState.requestDataChannel;
            if (dataChannel != null && (exchangeState.inBuf == null || !exchangeState.inBuf.hasData())) {
                Log.d(TAG, "[proxy->origin] " + exchangeState.id + " end of output");
                dataChannel.endStream();
            }
        }
    }

    @Override
    public int available() {
        synchronized (exchangeState) {
            final int available = exchangeState.outBuf != null ? exchangeState.outBuf.length() : 0;
            Log.d(TAG, "[client<-proxy] " + exchangeState.id + " output available: " + available);
            return available;
        }
    }

    @Override
    public void produce(final DataStreamChannel channel) throws IOException {
        synchronized (exchangeState) {
            Log.d(TAG, "[client<-proxy] " + exchangeState.id + " produce output");
            exchangeState.responseDataChannel = channel;

            if (exchangeState.outBuf != null) {
                if (exchangeState.outBuf.hasData()) {
                    final int bytesWritten = exchangeState.outBuf.write(channel);
                    Log.d(TAG, "[client<-proxy] " + exchangeState.id + " " + bytesWritten + " bytes sent");
                }
                if (exchangeState.outputEnd && !exchangeState.outBuf.hasData()) {
                    channel.endStream();
                    Log.d(TAG, "[client<-proxy] " + exchangeState.id + " end of output");
                }
                if (!exchangeState.outputEnd) {
                    final CapacityChannel capacityChannel = exchangeState.responseCapacityChannel;
                    if (capacityChannel != null) {
                        final int capacity = exchangeState.outBuf.capacity();
                        if (capacity > 0) {
                            Log.d(TAG, "[proxy->origin] " + exchangeState.id + " input capacity: " + capacity);
                            capacityChannel.update(capacity);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void failed(final Exception cause) {
        Log.d(TAG, "[client<-proxy] " + exchangeState.id + " " + cause.getMessage());
        if (!(cause instanceof ConnectionClosedException)) {
            cause.printStackTrace(System.err);
        }
        synchronized (exchangeState) {
            if (exchangeState.clientEndpoint != null) {
                exchangeState.clientEndpoint.releaseAndDiscard();
            }
        }
    }

    @Override
    public void releaseResources() {
        synchronized (exchangeState) {
            exchangeState.responseMessageChannel = null;
            exchangeState.responseDataChannel = null;
            exchangeState.requestCapacityChannel = null;
        }
    }

}
