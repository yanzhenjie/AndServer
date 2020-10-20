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
package com.yanzhenjie.andserver.framework.handler;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yanzhenjie.andserver.error.NotFoundException;
import com.yanzhenjie.andserver.framework.body.StringBody;
import com.yanzhenjie.andserver.framework.cross.CrossOrigin;
import com.yanzhenjie.andserver.framework.mapping.Addition;
import com.yanzhenjie.andserver.framework.mapping.Mapping;
import com.yanzhenjie.andserver.framework.view.BodyView;
import com.yanzhenjie.andserver.framework.view.View;
import com.yanzhenjie.andserver.http.HttpHeaders;
import com.yanzhenjie.andserver.http.HttpMethod;
import com.yanzhenjie.andserver.http.HttpRequest;
import com.yanzhenjie.andserver.http.HttpResponse;

import org.apache.httpcore.HttpStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Created by Zhenjie Yan on 10/17/20.
 */
public class OptionsHandler implements MethodHandler {

    public static final String INVALID_CORS_REQUEST = "Invalid CORS request.";

    private List<Mapping> mMappings;
    private Map<Mapping, RequestHandler> mMappingMap;

    private Mapping mMapping;
    private MethodHandler mHandler;

    public OptionsHandler(HttpRequest optionsRequest, List<Mapping> mappings, Map<Mapping, RequestHandler> mappingMap) {
        this.mMappings = mappings;
        this.mMappingMap = mappingMap;

        mMapping = mMappings.get(0);
        String requestMethod = optionsRequest.getHeader(HttpHeaders.Access_Control_Request_Method);
        if (!TextUtils.isEmpty(requestMethod)) {
            HttpMethod method = HttpMethod.reverse(requestMethod);
            Mapping exactMapping = MappingAdapter.findMappingByMethod(mMappings, method);
            if (exactMapping != null) {
                mMapping = exactMapping;
            }
        }

        mHandler = (MethodHandler) mMappingMap.get(mMapping);
    }

    @Nullable
    @Override
    public Addition getAddition() {
        return mHandler.getAddition();
    }

    @Nullable
    @Override
    public CrossOrigin getCrossOrigin() {
        return mHandler.getCrossOrigin();
    }

    @NonNull
    @Override
    public Mapping getMapping() {
        return mMapping;
    }

    @Override
    public View handle(@NonNull HttpRequest request, @NonNull HttpResponse response) throws Throwable {
        String requestOrigin = request.getHeader(HttpHeaders.ORIGIN);
        if (TextUtils.isEmpty(requestOrigin)) {
            return invalidCORS(response);
        }

        String requestMethodText = request.getHeader(HttpHeaders.Access_Control_Request_Method);
        if (TextUtils.isEmpty(requestMethodText)) {
            return invalidCORS(response);
        }

        HttpMethod requestMethod = HttpMethod.reverse(requestMethodText);
        Mapping mapping = MappingAdapter.findMappingByMethod(mMappings, requestMethod);
        if (mapping == null) {
            return invalidCORS(response);
        }

        MethodHandler handler = (MethodHandler) mMappingMap.get(mapping);
        if (handler == null) {
            throw new NotFoundException();
        }

        CrossOrigin crossOrigin = handler.getCrossOrigin();
        if (crossOrigin == null) {
            return invalidCORS(response);
        }

        List<HttpMethod> allowMethods = new ArrayList<>();
        Collections.addAll(allowMethods, crossOrigin.getMethods());
        List<HttpMethod> mappingMethods = mapping.getMethod().getRuleList();
        if (allowMethods.isEmpty()) {
            allowMethods.addAll(mappingMethods);
        }
        if (!allowMethods.contains(requestMethod)) {
            return invalidCORS(response);
        }

        List<String> allowOrigins = Arrays.asList(crossOrigin.getOrigins());
        if (!allowOrigins.isEmpty() && !allowOrigins.contains("*") && !allowOrigins.contains(requestOrigin)) {
            return invalidCORS(response);
        }

        List<String> allowedHeaders = Arrays.asList(crossOrigin.getAllowedHeaders());
        List<String> outHeaders = new ArrayList<>();
        String headerHeadersText = request.getHeader(HttpHeaders.Access_Control_Request_Headers);
        List<String> requestHeaders = new ArrayList<>();
        if (!TextUtils.isEmpty(headerHeadersText)) {
            StringTokenizer st = new StringTokenizer(headerHeadersText, ",");
            while (st.hasMoreTokens()) {
                String token = st.nextToken();
                token = token.trim();
                if (token.length() > 0) {
                    requestHeaders.add(token);
                }
            }
        }
        if (allowedHeaders.contains("*")) {
            if (requestHeaders.size() > 0) {
                outHeaders.addAll(requestHeaders);
            }
        } else if (allowedHeaders.size() > 0) {
            if (requestHeaders.size() > 0) {
                for (String allowedHeader: allowedHeaders) {
                    for (String requestHeader: requestHeaders) {
                        if (allowedHeader.equalsIgnoreCase(requestHeader)) {
                            outHeaders.add(requestHeader);
                        }
                    }
                }
                if (outHeaders.isEmpty()) {
                    return invalidCORS(response);
                }
            }
        } else if (requestHeaders.size() > 0) {
            outHeaders.addAll(requestHeaders);
        }

        String[] exposeHeaders = crossOrigin.getExposedHeaders();

        response.setHeader(HttpHeaders.Access_Control_Allow_Origin, requestOrigin);
        response.setHeader(HttpHeaders.Access_Control_Allow_Methods, TextUtils.join(", ", allowMethods));
        if (outHeaders.size() > 0) {
            response.setHeader(HttpHeaders.Access_Control_Allow_Headers, TextUtils.join(", ", outHeaders));
        }
        if (exposeHeaders.length > 0) {
            response.setHeader(HttpHeaders.Access_Control_Expose_Headers, TextUtils.join(", ", exposeHeaders));
        }

        boolean credentials = crossOrigin.isAllowCredentials();
        response.setHeader(HttpHeaders.Access_Control_Allow_Credentials, Boolean.toString(credentials));

        long maxAge = crossOrigin.getMaxAge();
        response.setHeader(HttpHeaders.Access_Control_Max_Age, Long.toString(maxAge));

        response.setHeader(HttpHeaders.ALLOW, TextUtils.join(", ", HttpMethod.values()));
        response.setHeader(HttpHeaders.VARY, HttpHeaders.ORIGIN);

        return new BodyView(new StringBody("OK"));
    }

    private View invalidCORS(HttpResponse response) {
        response.setStatus(HttpStatus.SC_FORBIDDEN);
        response.setHeader(HttpHeaders.ALLOW, TextUtils.join(", ", HttpMethod.values()));
        return new BodyView(new StringBody(INVALID_CORS_REQUEST));
    }

    @Override
    public String getETag(@NonNull HttpRequest request) throws Throwable {
        return mHandler.getETag(request);
    }

    @Override
    public long getLastModified(@NonNull HttpRequest request) throws Throwable {
        return mHandler.getLastModified(request);
    }
}