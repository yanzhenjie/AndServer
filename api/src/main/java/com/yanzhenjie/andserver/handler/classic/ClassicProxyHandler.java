/*
 * Copyright (C) 2020 Zhenjie Yan
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
package com.yanzhenjie.andserver.handler.classic;

import androidx.annotation.NonNull;

import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.impl.bootstrap.HttpRequester;
import org.apache.hc.core5.http.io.HttpRequestHandler;
import org.apache.hc.core5.http.message.BasicClassicHttpRequest;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.http.protocol.HttpCoreContext;
import org.apache.hc.core5.util.Timeout;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

/**
 * Created by Zhenjie Yan on 3/7/20.
 */
public class ClassicProxyHandler implements HttpRequestHandler {

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

    private final HttpHost mTargetHost;
    private final HttpRequester mRequester;

    public ClassicProxyHandler(@NonNull HttpHost targetHost, @NonNull HttpRequester requester) {
        this.mTargetHost = targetHost;
        this.mRequester = requester;
    }

    @Override
    public void handle(ClassicHttpRequest incomingRequest, ClassicHttpResponse outgoingResponse, HttpContext context)
            throws HttpException, IOException {

        final HttpCoreContext clientContext = HttpCoreContext.create();
        final ClassicHttpRequest outgoingRequest = new BasicClassicHttpRequest(
                incomingRequest.getMethod(),
                mTargetHost,
                incomingRequest.getPath());
        for (final Iterator<Header> it = incomingRequest.headerIterator(); it.hasNext(); ) {
            final Header header = it.next();
            if (!HOP_BY_HOP.contains(header.getName().toLowerCase(Locale.ROOT))) {
                outgoingRequest.addHeader(header);
            }
        }
        outgoingRequest.setEntity(incomingRequest.getEntity());

        final ClassicHttpResponse incomingResponse = mRequester.execute(
                mTargetHost, outgoingRequest, Timeout.ofMinutes(1), clientContext);

        outgoingResponse.setCode(incomingResponse.getCode());
        for (final Iterator<Header> it = incomingResponse.headerIterator(); it.hasNext(); ) {
            final Header header = it.next();
            if (!HOP_BY_HOP.contains(header.getName().toLowerCase(Locale.ROOT))) {
                outgoingResponse.setHeader(header);
            }
        }
        outgoingResponse.setEntity(incomingResponse.getEntity());
    }
}