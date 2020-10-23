/*
 * Copyright 2018 Zhenjie Yan.
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

import com.yanzhenjie.andserver.framework.ETag;
import com.yanzhenjie.andserver.framework.LastModified;
import com.yanzhenjie.andserver.framework.body.StringBody;
import com.yanzhenjie.andserver.framework.cross.CrossOrigin;
import com.yanzhenjie.andserver.framework.mapping.Addition;
import com.yanzhenjie.andserver.framework.mapping.Mapping;
import com.yanzhenjie.andserver.framework.mapping.Path;
import com.yanzhenjie.andserver.framework.view.BodyView;
import com.yanzhenjie.andserver.framework.view.View;
import com.yanzhenjie.andserver.http.HttpHeaders;
import com.yanzhenjie.andserver.http.HttpMethod;
import com.yanzhenjie.andserver.http.HttpRequest;
import com.yanzhenjie.andserver.http.HttpResponse;

import org.apache.httpcore.HttpStatus;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Zhenjie Yan on 2018/9/9.
 */
public abstract class MappingHandler implements MethodHandler {

    private final Object mHost;
    private final Mapping mMapping;
    private final Addition mAddition;
    private final CrossOrigin mCrossOrigin;

    public MappingHandler(@NonNull Object host, @NonNull Mapping mapping, @NonNull Addition addition,
                          @Nullable CrossOrigin crossOrigin) {
        this.mHost = host;
        this.mMapping = mapping;
        this.mAddition = addition;
        this.mCrossOrigin = crossOrigin;
    }

    @Override
    public String getETag(@NonNull HttpRequest request) throws Throwable {
        Object o = getHost();
        if (o instanceof ETag) {
            return ((ETag) o).getETag(request);
        }
        return null;
    }

    @Override
    public long getLastModified(@NonNull HttpRequest request) throws Throwable {
        Object o = getHost();
        if (o instanceof LastModified) {
            return ((LastModified) o).getLastModified(request);
        }
        return -1;
    }

    @NonNull
    @Override
    public Addition getAddition() {
        return mAddition;
    }

    @Nullable
    @Override
    public CrossOrigin getCrossOrigin() {
        return mCrossOrigin;
    }

    @NonNull
    @Override
    public Mapping getMapping() {
        return mMapping;
    }

    @NonNull
    protected Object getHost() {
        return mHost;
    }

    /**
     * Get the path to match the request.
     *
     * @param httpPath http path.
     *
     * @return the path of handler.
     */
    @NonNull
    protected Map<String, String> getPathVariable(@NonNull String httpPath) {
        List<Path.Segment> httpSegments = Path.pathToList(httpPath);
        List<Path.Rule> ruleList = mMapping.getPath().getRuleList();
        for (Path.Rule rule: ruleList) {
            List<Path.Segment> segments = rule.getSegments();
            if (httpSegments.size() != segments.size()) {
                continue;
            }

            String path = Path.listToPath(segments);
            if (path.equals(httpPath)) {
                return Collections.emptyMap();
            }

            boolean matches = true;
            boolean isBlurred = false;
            for (int i = 0; i < segments.size(); i++) {
                Path.Segment segment = segments.get(i);
                boolean blurred = segment.isBlurred();
                isBlurred = isBlurred || blurred;
                if (!segment.equals(httpSegments.get(i)) && !blurred) {
                    matches = false;
                    break;
                }
            }

            if (matches && isBlurred) {
                Map<String, String> map = new HashMap<>();
                for (int i = 0; i < segments.size(); i++) {
                    Path.Segment segment = segments.get(i);
                    if (segment.isBlurred()) {
                        Path.Segment httpSegment = httpSegments.get(i);

                        String key = segment.getValue();
                        key = key.substring(1, key.length() - 1);
                        map.put(key, httpSegment.getValue());
                    }
                }
                return map;
            }
        }

        return Collections.emptyMap();
    }

    @Override
    public View handle(@NonNull HttpRequest request, @NonNull HttpResponse response) throws Throwable {
        String origin = request.getHeader(HttpHeaders.ORIGIN);
        if (!TextUtils.isEmpty(origin) && mCrossOrigin != null) {
            HttpMethod method = request.getMethod();

            List<HttpMethod> allowMethods = Arrays.asList(mCrossOrigin.getMethods());
            if (!allowMethods.isEmpty() && !allowMethods.contains(method)) {
                return invalidCORS(response);
            }

            response.setHeader(HttpHeaders.Access_Control_Allow_Origin, origin);
            boolean credentials = mCrossOrigin.isAllowCredentials();
            response.setHeader(HttpHeaders.Access_Control_Allow_Credentials, Boolean.toString(credentials));
            response.setHeader(HttpHeaders.VARY, HttpHeaders.ORIGIN);
        }

        return onHandle(request, response);
    }

    private View invalidCORS(HttpResponse response, HttpMethod... methods) {
        response.setStatus(HttpStatus.SC_FORBIDDEN);
        if (methods != null && methods.length > 0) {
            response.setHeader(HttpHeaders.ALLOW, TextUtils.join(", ", methods));
        }
        return new BodyView(new StringBody(OptionsHandler.INVALID_CORS_REQUEST));
    }

    protected abstract View onHandle(HttpRequest request, HttpResponse response) throws Throwable;
}