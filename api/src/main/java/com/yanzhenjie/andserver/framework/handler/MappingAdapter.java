/*
 * Copyright 2018 Yan Zhenjie.
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

import android.support.annotation.NonNull;

import com.yanzhenjie.andserver.error.ContentNotAcceptableException;
import com.yanzhenjie.andserver.error.ContentNotSupportedException;
import com.yanzhenjie.andserver.error.HeaderValidateException;
import com.yanzhenjie.andserver.error.MethodNotSupportException;
import com.yanzhenjie.andserver.error.ParamValidateException;
import com.yanzhenjie.andserver.http.HttpContext;
import com.yanzhenjie.andserver.http.HttpMethod;
import com.yanzhenjie.andserver.http.HttpRequest;
import com.yanzhenjie.andserver.mapping.Mapping;
import com.yanzhenjie.andserver.mapping.Mime;
import com.yanzhenjie.andserver.mapping.Pair;
import com.yanzhenjie.andserver.mapping.Path;
import com.yanzhenjie.andserver.util.MediaType;
import com.yanzhenjie.andserver.util.Patterns;
import com.yanzhenjie.andserver.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by YanZhenjie on 2018/9/8.
 */
public abstract class MappingAdapter implements HandlerAdapter, Patterns {

    @Override
    public boolean intercept(@NonNull HttpRequest request) {
        String httpPath = request.getPath();
        HttpMethod httpMethod = request.getMethod();
        List<Mapping> mappings = getMappings(httpPath);
        if (mappings.isEmpty()) return false;

        Mapping mapping = validateMethod(mappings, httpMethod);

        Pair param = mapping.getParam();
        if (param != null) validateParams(param, request);

        Pair header = mapping.getHeader();
        if (header != null) validateHeaders(header, request);

        Mime consume = mapping.getConsume();
        if (consume != null) validateConsume(consume, request);

        Mime produce = mapping.getProduce();
        if (produce != null) validateProduce(produce, request);

        return true;
    }

    @Override
    public RequestHandler getHandler(@NonNull HttpRequest request) {
        String httpPath = request.getPath();
        HttpMethod httpMethod = request.getMethod();
        List<Mapping> mappings = getMappings(httpPath);
        if (mappings.isEmpty()) return null;

        Mapping mapping = validateMethod(mappings, httpMethod);

        Mime mime = mapping.getProduce();
        if (mime != null) {
            List<Mime.Rule> produces = mime.getRuleList();
            MediaType mediaType = null;
            for (Mime.Rule produce : produces) {
                String text = produce.toString();
                if (!text.startsWith("!")) {
                    mediaType = produce;
                    break;
                }
            }
            request.setAttribute(HttpContext.RESPONSE_PRODUCE_TYPE, mediaType);
        }
        return getMappingMap().get(mapping);
    }

    /**
     * Get the mapping corresponding to the path.
     *
     * @param httpPath http path.
     *
     * @return a {@link Mapping} object, or {@code null} if the path is not mapped.
     */
    @NonNull
    private List<Mapping> getMappings(String httpPath) {
        List<Mapping> returned = new ArrayList<>();
        List<Path.Segment> httpSegments = Path.pathToList(httpPath);
        Map<Mapping, RequestHandler> mappings = getMappingMap();
        for (Mapping mapping : mappings.keySet()) {
            List<Path.Rule> paths = mapping.getPath().getRuleList();
            for (Path.Rule path : paths) {
                List<Path.Segment> segments = path.getSegments();
                if (httpSegments.size() != segments.size()) continue;

                String pathStr = Path.listToPath(segments);
                if (pathStr.equals(httpPath)) {
                    returned.add(mapping);
                    break; // Validate next mapping.
                }

                boolean matches = true;
                for (int i = 0; i < segments.size(); i++) {
                    Path.Segment segment = segments.get(i);
                    if (!segment.equals(httpSegments.get(i)) && !segment.isBlurred()) {
                        matches = false;
                        break;
                    }
                }
                if (matches) returned.add(mapping);
            }
        }
        return returned;
    }

    private Mapping validateMethod(List<Mapping> mappings, HttpMethod httpMethod) {
        for (Mapping mapping : mappings) {
            List<HttpMethod> mappingMethods = mapping.getMethod().getRuleList();
            if (mappingMethods.contains(httpMethod)) {
                return mapping;
            }
        }
        throw new MethodNotSupportException(httpMethod);
    }

    private void validateParams(Pair param, HttpRequest request) {
        List<Pair.Rule> rules = param.getRuleList();
        for (Pair.Rule rule : rules) {
            String key = rule.getKey();
            List<String> keys = request.getParameterNames();
            String value = rule.getValue();
            List<String> values = request.getParameters(key);
            if (rule.isNoKey()) {
                if (keys.contains(key)) {
                    throw new ParamValidateException(String.format("The parameter [%s] is not allowed.", key));
                }
            } else if (rule.isNoValue()) {
                if (values.contains(value)) {
                    throw new ParamValidateException(
                        String.format("The value of parameter %s cannot be %s.", key, value));
                }
            } else if (!StringUtils.isEmpty(key) && !StringUtils.isEmpty(value)) {
                if (!keys.contains(key) || !values.contains(value)) {
                    throw new ParamValidateException(
                        String.format("The value of parameter %s is missing or wrong.", key));
                }
            } else if (!StringUtils.isEmpty(key) && StringUtils.isEmpty(value)) {
                if (!keys.contains(key)) {
                    throw new ParamValidateException(String.format("The parameter %s is missing.", key));
                }
            }
        }
    }

    private void validateHeaders(Pair header, HttpRequest request) {
        List<Pair.Rule> rules = header.getRuleList();
        for (Pair.Rule rule : rules) {
            String key = rule.getKey();
            List<String> keys = request.getHeaderNames();
            String value = rule.getValue();
            List<String> values = request.getHeaders(key);
            if (rule.isNoKey()) {
                if (keys.contains(key)) {
                    throw new HeaderValidateException(String.format("The header [%s] is not allowed.", key));
                }
            } else if (rule.isNoValue()) {
                if (values.contains(value)) {
                    throw new HeaderValidateException(
                        String.format("The value of header %s cannot be %s.", key, value));
                }
            } else if (!StringUtils.isEmpty(key) && !StringUtils.isEmpty(value) &&
                (!keys.contains(key) || !values.contains(value))) {
                throw new HeaderValidateException(String.format("The value of header %s is missing or wrong.", key));
            } else if (!StringUtils.isEmpty(key) && StringUtils.isEmpty(value)) {
                if (!keys.contains(key)) {
                    throw new HeaderValidateException(String.format("The header %s is missing.", key));
                }
            }
        }
    }

    private void validateConsume(Mime mime, HttpRequest request) {
        List<Mime.Rule> rules = mime.getRuleList();
        MediaType contentType = request.getContentType();
        for (Mime.Rule rule : rules) {
            String type = rule.getType();
            boolean nonContent = type.startsWith("!");
            if (nonContent) {
                type = type.substring(1);
            }
            MediaType consume = new MediaType(type, rule.getSubtype());

            if (nonContent) {
                if (consume.equalsExcludeParameter(contentType)) {
                    throw new ContentNotSupportedException(contentType);
                }
            } else {
                if (contentType == null || !contentType.includes(consume)) {
                    throw new ContentNotSupportedException(contentType);
                }
            }
        }
    }

    private void validateProduce(Mime mime, HttpRequest request) {
        List<Mime.Rule> rules = mime.getRuleList();
        List<MediaType> accepts = request.getAccepts();
        for (Mime.Rule rule : rules) {
            String type = rule.getType();
            boolean nonContent = type.startsWith("!");
            if (nonContent) {
                type = type.substring(1);
            }
            MediaType produce = new MediaType(type, rule.getSubtype());

            boolean exclude = false;
            for (MediaType accept : accepts) {
                if (accept.includes(produce)) {
                    exclude = true;
                }
            }
            if (nonContent && exclude) {
                throw new ContentNotAcceptableException();
            }
            if (!nonContent && !exclude) {
                throw new ContentNotAcceptableException();
            }
        }
    }

    /**
     * Get all the mappings for this adapter.
     *
     * @return all mappings, non-null, non-empty.
     */
    @NonNull
    protected abstract Map<Mapping, RequestHandler> getMappingMap();

    /**
     * Get the host of the {@code HandlerAdapter}.
     *
     * @return the host of the adapter.
     */
    @NonNull
    protected abstract Object getHost();
}