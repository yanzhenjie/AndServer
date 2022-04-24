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
import org.apache.hc.core5.http.message.BasicHttpRequest;
import org.apache.hc.core5.http.message.BasicHttpResponse;
import org.apache.hc.core5.http.nio.AsyncClientEndpoint;
import org.apache.hc.core5.http.nio.AsyncClientExchangeHandler;
import org.apache.hc.core5.http.nio.CapacityChannel;
import org.apache.hc.core5.http.nio.DataStreamChannel;
import org.apache.hc.core5.http.nio.RequestChannel;
import org.apache.hc.core5.http.nio.ResponseChannel;
import org.apache.hc.core5.http.protocol.HttpContext;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class ProxyOutgoingExchangeHandler implements AsyncClientExchangeHandler {

    public static final String SUB_TAG = "ProxyOutgoingExchangeHandler";
    public static final String TAG = AndServer.genAndServerTag(SUB_TAG);

    private final static Set<String> HOP_BY_HOP = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            HttpHeaders.HOST.toLowerCase(Locale.ROOT),
            HttpHeaders.CONTENT_LENGTH.toLowerCase(Locale.ROOT),
            HttpHeaders.TRANSFER_ENCODING.toLowerCase(Locale.ROOT),
            HttpHeaders.CONNECTION.toLowerCase(Locale.ROOT),
            HttpHeaders.KEEP_ALIVE.toLowerCase(Locale.ROOT),
            HttpHeaders.PROXY_AUTHENTICATE.toLowerCase(Locale.ROOT),
            HttpHeaders.TE.toLowerCase(Locale.ROOT),
            HttpHeaders.TRAILER.toLowerCase(Locale.ROOT),
            HttpHeaders.UPGRADE.toLowerCase(Locale.ROOT))));

    private final HttpHost targetHost;
    private final AsyncClientEndpoint clientEndpoint;
    private final AsyncReverseProxyServer.ProxyExchangeState exchangeState;

    private final int bufferSize;

    ProxyOutgoingExchangeHandler(
            final HttpHost targetHost,
            final AsyncClientEndpoint clientEndpoint,
            final AsyncReverseProxyServer.ProxyExchangeState exchangeState,
            int bufferSize) {
        this.targetHost = targetHost;
        this.clientEndpoint = clientEndpoint;
        this.exchangeState = exchangeState;
        this.bufferSize = bufferSize;
    }

    @Override
    public void produceRequest(
            final RequestChannel channel, final HttpContext httpContext) throws HttpException, IOException {
        synchronized (exchangeState) {
            final HttpRequest incomingRequest = exchangeState.request;
            final EntityDetails entityDetails = exchangeState.requestEntityDetails;
            final HttpRequest outgoingRequest = new BasicHttpRequest(
                    incomingRequest.getMethod(),
                    targetHost,
                    incomingRequest.getPath());
            for (final Iterator<Header> it = incomingRequest.headerIterator(); it.hasNext(); ) {
                final Header header = it.next();
                if (!HOP_BY_HOP.contains(header.getName().toLowerCase(Locale.ROOT))) {
                    outgoingRequest.addHeader(header);
                }
            }

            Log.d(TAG, "[proxy->origin] " + exchangeState.id + " " +
                    outgoingRequest.getMethod() + " " + outgoingRequest.getRequestUri());

            channel.sendRequest(outgoingRequest, entityDetails, httpContext);
        }
    }

    @Override
    public int available() {
        synchronized (exchangeState) {
            final int available = exchangeState.inBuf != null ? exchangeState.inBuf.length() : 0;
            Log.d(TAG, "[proxy->origin] " + exchangeState.id + " output available: " + available);
            return available;
        }
    }

    @Override
    public void produce(final DataStreamChannel channel) throws IOException {
        synchronized (exchangeState) {
            Log.d(TAG, "[proxy->origin] " + exchangeState.id + " produce output");
            exchangeState.requestDataChannel = channel;
            if (exchangeState.inBuf != null) {
                if (exchangeState.inBuf.hasData()) {
                    final int bytesWritten = exchangeState.inBuf.write(channel);
                    Log.d(TAG, "[proxy->origin] " + exchangeState.id + " " + bytesWritten + " bytes sent");
                }
                if (exchangeState.inputEnd && !exchangeState.inBuf.hasData()) {
                    channel.endStream();
                    Log.d(TAG, "[proxy->origin] " + exchangeState.id + " end of output");
                }
                if (!exchangeState.inputEnd) {
                    final CapacityChannel capacityChannel = exchangeState.requestCapacityChannel;
                    if (capacityChannel != null) {
                        final int capacity = exchangeState.inBuf.capacity();
                        if (capacity > 0) {
                            Log.d(TAG, "[client<-proxy] " + exchangeState.id + " input capacity: " + capacity);
                            capacityChannel.update(capacity);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void consumeInformation(final HttpResponse response, final HttpContext httpContext) throws HttpException, IOException {
        // ignore
    }

    @Override
    public void consumeResponse(
            final HttpResponse incomingResponse,
            final EntityDetails entityDetails, final HttpContext httpContext) throws HttpException, IOException {
        synchronized (exchangeState) {
            Log.d(TAG, "[proxy<-origin] " + exchangeState.id + " status " + incomingResponse.getCode());
            if (entityDetails == null) {
                Log.d(TAG, "[proxy<-origin] " + exchangeState.id + " end of input");
            }

            final HttpResponse outgoingResponse = new BasicHttpResponse(incomingResponse.getCode());
            for (final Iterator<Header> it = incomingResponse.headerIterator(); it.hasNext(); ) {
                final Header header = it.next();
                if (!HOP_BY_HOP.contains(header.getName().toLowerCase(Locale.ROOT))) {
                    outgoingResponse.addHeader(header);
                }
            }

            exchangeState.response = outgoingResponse;
            exchangeState.responseEntityDetails = entityDetails;
            exchangeState.outputEnd = entityDetails == null;

            final ResponseChannel responseChannel = exchangeState.responseMessageChannel;
            if (responseChannel != null) {
                // responseChannel can be null under load.
                responseChannel.sendResponse(outgoingResponse, entityDetails, httpContext);
            }

            Log.d(TAG, "[client<-proxy] " + exchangeState.id + " status " + outgoingResponse.getCode());
            if (entityDetails == null) {
                Log.d(TAG, "[client<-proxy] " + exchangeState.id + " end of output");
                clientEndpoint.releaseAndReuse();
            }
        }
    }

    @Override
    public void updateCapacity(final CapacityChannel capacityChannel) throws IOException {
        synchronized (exchangeState) {
            exchangeState.responseCapacityChannel = capacityChannel;
            final int capacity = exchangeState.outBuf != null ? exchangeState.outBuf.capacity() : bufferSize;
            if (capacity > 0) {
                Log.d(TAG, "[proxy->origin] " + exchangeState.id + " input capacity: " + capacity);
                capacityChannel.update(capacity);
            }
        }
    }

    @Override
    public void consume(final ByteBuffer src) throws IOException {
        synchronized (exchangeState) {
            Log.d(TAG, "[proxy<-origin] " + exchangeState.id + " " + src.remaining() + " bytes received");
            final DataStreamChannel dataChannel = exchangeState.responseDataChannel;
            if (dataChannel != null && exchangeState.outBuf != null) {
                if (exchangeState.outBuf.hasData()) {
                    final int bytesWritten = exchangeState.outBuf.write(dataChannel);
                    Log.d(TAG, "[client<-proxy] " + exchangeState.id + " " + bytesWritten + " bytes sent");
                }
                if (!exchangeState.outBuf.hasData()) {
                    final int bytesWritten = dataChannel.write(src);
                    Log.d(TAG, "[client<-proxy] " + exchangeState.id + " " + bytesWritten + " bytes sent");
                }
            }
            if (src.hasRemaining()) {
                if (exchangeState.outBuf == null) {
                    exchangeState.outBuf = new AsyncReverseProxyServer.ProxyBuffer(bufferSize);
                }
                exchangeState.outBuf.put(src);
            }
            final int capacity = exchangeState.outBuf != null ? exchangeState.outBuf.capacity() : bufferSize;
            Log.d(TAG, "[proxy->origin] " + exchangeState.id + " input capacity: " + capacity);
            if (dataChannel != null) {
                dataChannel.requestOutput();
            }
        }
    }

    @Override
    public void streamEnd(final List<? extends Header> trailers) throws HttpException, IOException {
        synchronized (exchangeState) {
            Log.d(TAG, "[proxy<-origin] " + exchangeState.id + " end of input");
            exchangeState.outputEnd = true;
            final DataStreamChannel dataChannel = exchangeState.responseDataChannel;
            if (dataChannel != null && (exchangeState.outBuf == null || !exchangeState.outBuf.hasData())) {
                Log.d(TAG, "[client<-proxy] " + exchangeState.id + " end of output");
                dataChannel.endStream();
                clientEndpoint.releaseAndReuse();
            }
        }
    }

    @Override
    public void cancel() {
        clientEndpoint.releaseAndDiscard();
    }

    @Override
    public void failed(final Exception cause) {
        Log.e(TAG, "[client<-proxy] " + exchangeState.id + " " + cause.getMessage());
        if (!(cause instanceof ConnectionClosedException)) {
            cause.printStackTrace(System.err);
        }
        synchronized (exchangeState) {
            if (exchangeState.response == null) {
                final int status = cause instanceof IOException ? HttpStatus.SC_SERVICE_UNAVAILABLE : HttpStatus.SC_INTERNAL_SERVER_ERROR;
                final HttpResponse outgoingResponse = new BasicHttpResponse(status);
                outgoingResponse.addHeader(HttpHeaders.CONNECTION, HeaderElements.CLOSE);
                exchangeState.response = outgoingResponse;

                final ByteBuffer msg = StandardCharsets.US_ASCII.encode(CharBuffer.wrap(cause.getMessage()));
                final int contentLen = msg.remaining();
                exchangeState.outBuf = new AsyncReverseProxyServer.ProxyBuffer(1024);
                exchangeState.outBuf.put(msg);
                exchangeState.outputEnd = true;

                Log.d(TAG, "[client<-proxy] " + exchangeState.id + " status " + outgoingResponse.getCode());

                try {
                    final EntityDetails entityDetails = new BasicEntityDetails(contentLen, ContentType.TEXT_PLAIN);
                    exchangeState.responseMessageChannel.sendResponse(outgoingResponse, entityDetails, null);
                } catch (final HttpException | IOException ignore) {
                    // ignore
                }
            } else {
                exchangeState.outputEnd = true;
            }
            clientEndpoint.releaseAndDiscard();
        }
    }

    @Override
    public void releaseResources() {
        synchronized (exchangeState) {
            exchangeState.requestDataChannel = null;
            exchangeState.responseCapacityChannel = null;
            clientEndpoint.releaseAndDiscard();
        }
    }
}