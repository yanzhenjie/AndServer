/*
 * Copyright © 2017 Yan Zhenjie.
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

import com.yanzhenjie.andserver.annotation.RequestMapping;
import com.yanzhenjie.andserver.exception.BaseException;
import com.yanzhenjie.andserver.exception.MethodNotSupported;
import com.yanzhenjie.andserver.exception.NotFoundException;
import com.yanzhenjie.andserver.exception.resolver.ExceptionResolver;
import com.yanzhenjie.andserver.exception.resolver.SimpleExceptionResolver;
import com.yanzhenjie.andserver.filter.Filter;
import com.yanzhenjie.andserver.interceptor.Interceptor;
import com.yanzhenjie.andserver.website.WebSite;

import org.apache.httpcore.HttpException;
import org.apache.httpcore.HttpRequest;
import org.apache.httpcore.HttpResponse;
import org.apache.httpcore.protocol.HttpContext;
import org.apache.httpcore.protocol.HttpRequestHandler;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.yanzhenjie.andserver.util.HttpRequestParser.getRequestPath;

/**
 * Created by Yan Zhenjie on 2017/3/15.
 */
class DispatchRequestHandler implements HttpRequestHandler {

    private static ExceptionResolver sDefaultExceptionResolver = new SimpleExceptionResolver();

    private Interceptor mInterceptor;
    private WebSite mWebSite;
    private Map<String, RequestHandler> mRequestHandlerMapper = new LinkedHashMap<>();
    private Filter mFilter;
    private ExceptionResolver mExceptionResolver = sDefaultExceptionResolver;

    DispatchRequestHandler() {
    }

    void setInterceptor(Interceptor interceptor) {
        mInterceptor = interceptor;
    }

    void setWebSite(WebSite webSite) {
        this.mWebSite = webSite;
    }

    void registerRequestHandler(String path, RequestHandler handler) {
        mRequestHandlerMapper.put(path, handler);
    }

    void setFilter(Filter filter) {
        this.mFilter = filter;
    }

    void setExceptionResolver(ExceptionResolver exceptionResolver) {
        mExceptionResolver = exceptionResolver;
    }

    @Override
    public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
        try {
            if (mInterceptor != null && mInterceptor.onBeforeExecute(request, response, context))
                return;

            RequestHandler handler = getRequestHandler(request, context);
            if (handler == null) {
                String path = getRequestPath(request);
                throw new NotFoundException(path);
            } else {
                handleRequest(handler, request, response, context);
            }

            if (mInterceptor != null)
                mInterceptor.onAfterExecute(request, response, context);
        } catch (Exception e) {
            try {
                mExceptionResolver.resolveException(e, request, response, context);
            } catch (Exception ee) {
                sDefaultExceptionResolver.resolveException(e, request, response, context);
            }
        }
    }

    /**
     * Handle Request with handler.
     */
    private void handleRequest(RequestHandler handler, HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
        verifyHandler(request, handler);
        if (mFilter != null) {
            mFilter.doFilter(handler, request, response, context);
        } else {
            handler.handle(request, response, context);
        }
    }

    /**
     * The processor that gets the current request.
     */
    private RequestHandler getRequestHandler(HttpRequest request, HttpContext context) throws HttpException, IOException {
        String path = getRequestPath(request);
        if (mWebSite != null && mWebSite.intercept(request, context)) {
            return mWebSite;
        }
        return mRequestHandlerMapper.get(path);
    }

    private void verifyHandler(HttpRequest request, RequestHandler handler) throws BaseException {
        RequestMethod requestMethod = RequestMethod.reverse(request.getRequestLine().getMethod());
        Class<?> clazz = handler.getClass();
        try {
            Method handlerMethod = clazz.getMethod("handle", HttpRequest.class, HttpResponse.class, HttpContext.class);
            RequestMapping requestMapping = handlerMethod.getAnnotation(RequestMapping.class);
            if (requestMapping != null) {
                RequestMethod[] requestMethods = requestMapping.method();
                List<RequestMethod> requestMethodList = Arrays.asList(requestMethods);
                if (!requestMethodList.contains(requestMethod)) {
                    throw new MethodNotSupported(requestMethod);
                }
            }
        } catch (NoSuchMethodException ignored) {
        }
    }
}