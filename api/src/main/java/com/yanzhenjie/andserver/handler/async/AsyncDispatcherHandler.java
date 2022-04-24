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
package com.yanzhenjie.andserver.handler.async;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;

import com.yanzhenjie.andserver.AndServer;
import com.yanzhenjie.andserver.framework.ExceptionResolver;
import com.yanzhenjie.andserver.framework.HandlerInterceptor;
import com.yanzhenjie.andserver.framework.MessageConverter;
import com.yanzhenjie.andserver.framework.config.Multipart;
import com.yanzhenjie.andserver.framework.handler.HandlerAdapter;
import com.yanzhenjie.andserver.handler.BasicDispatcherHandler;
import com.yanzhenjie.andserver.handler.DispatcherHandler;
import com.yanzhenjie.andserver.http.HttpRequest;
import com.yanzhenjie.andserver.http.HttpResponse;
import com.yanzhenjie.andserver.http.RequestDispatcher;
import com.yanzhenjie.andserver.http.ResponseBody;
import com.yanzhenjie.andserver.http.StandardContext;
import com.yanzhenjie.andserver.http.StandardRequest;
import com.yanzhenjie.andserver.http.StandardResponse;
import com.yanzhenjie.andserver.http.session.SessionManager;
import com.yanzhenjie.andserver.http.session.StandardSessionManager;

import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.EntityDetails;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.impl.nio.DefaultHttpResponseFactory;
import org.apache.hc.core5.http.io.entity.ByteArrayEntity;
import org.apache.hc.core5.http.nio.AsyncRequestConsumer;
import org.apache.hc.core5.http.nio.AsyncServerRequestHandler;
import org.apache.hc.core5.http.nio.entity.BasicAsyncEntityConsumer;
import org.apache.hc.core5.http.nio.support.AbstractAsyncRequesterConsumer;
import org.apache.hc.core5.http.nio.support.AbstractServerExchangeHandler;
import org.apache.hc.core5.http.nio.support.BasicResponseProducer;

import java.io.IOException;

/**
 * Created by ISNing on 2022/4/26.
 */
public class AsyncDispatcherHandler extends AbstractServerExchangeHandler<StandardRequest> implements DispatcherHandler {

    public static final String SUB_TAG = "ClassicDispatcherHandler";
    public static final String TAG = AndServer.genAndServerTag(SUB_TAG);

    private final SessionManager mSessionManager;
    private final DispatcherHandler mDispatcherHandler;

    public AsyncDispatcherHandler(Context context) {
        super();
        this.mDispatcherHandler = new BasicDispatcherHandler(context);
        this.mSessionManager = new StandardSessionManager(context);
    }

    @Override
    protected AsyncRequestConsumer<StandardRequest> supplyConsumer(org.apache.hc.core5.http.HttpRequest request, EntityDetails entityDetails, org.apache.hc.core5.http.protocol.HttpContext context) throws HttpException {
        return new AbstractAsyncRequesterConsumer<StandardRequest, byte[]>(new BasicAsyncEntityConsumer()) {
            @Override
            protected StandardRequest buildResult(org.apache.hc.core5.http.HttpRequest request, byte[] entity, ContentType contentType) {
                return new StandardRequest(request, entity == null ? null : new ByteArrayEntity(entity, contentType), new StandardContext(context), AsyncDispatcherHandler.this, mSessionManager);
            }
        };
    }

    @Override
    protected void handle(StandardRequest requestWrapped, AsyncServerRequestHandler.ResponseTrigger responseTrigger, org.apache.hc.core5.http.protocol.HttpContext context) throws HttpException, IOException {
        org.apache.hc.core5.http.HttpResponse response = DefaultHttpResponseFactory.INSTANCE.newHttpResponse(HttpStatus.SC_OK);
        HttpResponse responseWrapped = new StandardResponse(response);
        mDispatcherHandler.handle(requestWrapped, responseWrapped);
        ResponseBody body = responseWrapped.getBody();
        responseTrigger.submitResponse(new BasicResponseProducer(response, body == null ? null : body.toEntityProducer()), context);
    }

    @Override
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public void handle(HttpRequest request, HttpResponse response) {
        mDispatcherHandler.handle(request, response);
    }

    @Override
    public void addAdapter(@NonNull HandlerAdapter adapter) {
        mDispatcherHandler.addAdapter(adapter);
    }

    @Override
    public void addInterceptor(@NonNull HandlerInterceptor interceptor) {
        mDispatcherHandler.addInterceptor(interceptor);
    }

    @Override
    public void setConverter(MessageConverter converter) {
        mDispatcherHandler.setConverter(converter);
    }

    @Override
    public void setResolver(@NonNull ExceptionResolver resolver) {
        mDispatcherHandler.setResolver(resolver);
    }

    @Override
    public void setMultipart(Multipart multipart) {
        mDispatcherHandler.setMultipart(multipart);
    }

    @Override
    @Nullable
    public RequestDispatcher getRequestDispatcher(HttpRequest request, String path) {
        return mDispatcherHandler.getRequestDispatcher(request, path);
    }
}