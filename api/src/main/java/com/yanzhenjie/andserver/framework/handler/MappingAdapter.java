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

import com.yanzhenjie.andserver.error.ContentNotAcceptableException;
import com.yanzhenjie.andserver.error.ContentNotSupportedException;
import com.yanzhenjie.andserver.error.HeaderValidateException;
import com.yanzhenjie.andserver.error.MethodNotSupportException;
import com.yanzhenjie.andserver.error.ParamValidateException;
import com.yanzhenjie.andserver.framework.mapping.Mapping;
import com.yanzhenjie.andserver.framework.mapping.Mime;
import com.yanzhenjie.andserver.framework.mapping.Pair;
import com.yanzhenjie.andserver.framework.mapping.Path;
import com.yanzhenjie.andserver.http.HttpContext;
import com.yanzhenjie.andserver.http.HttpMethod;
import com.yanzhenjie.andserver.http.HttpRequest;
import com.yanzhenjie.andserver.util.MediaType;
import com.yanzhenjie.andserver.util.Patterns;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Zhenjie Yan on 2018/9/8.
 */
public abstract class MappingAdapter implements HandlerAdapter, Patterns {

    @Override
    public boolean intercept(@NonNull HttpRequest request) {
        List<Path.Segment> pathSegments = Path.pathToList(request.getPath());

        List<Mapping> mappings = getExactMappings(pathSegments);
        if (mappings.isEmpty()) {
            mappings = getBlurredMappings(pathSegments);
        }
        if (mappings.isEmpty()) {
            return false;
        }

        HttpMethod method = request.getMethod();
        if (method.equals(HttpMethod.OPTIONS)) {
            return true;
        }

        Mapping mapping = MappingAdapter.findMappingByMethod(mappings, method);
        if (mapping == null) {
            MethodNotSupportException exception = new MethodNotSupportException(method);
            List<HttpMethod> methods = MappingAdapter.findSupportMethods(mappings);
            exception.setMethods(methods);
            throw exception;
        }

        Pair param = mapping.getParam();
        if (param != null) {
            validateParams(param, request);
        }

        Pair header = mapping.getHeader();
        if (header != null) {
            validateHeaders(header, request);
        }

        Mime consume = mapping.getConsume();
        if (consume != null) {
            validateConsume(consume, request);
        }

        Mime produce = mapping.getProduce();
        if (produce != null) {
            validateProduce(produce, request);
        }

        return true;
    }

    @Nullable
    @Override
    public RequestHandler getHandler(@NonNull HttpRequest request) {
        List<Path.Segment> pathSegments = Path.pathToList(request.getPath());

        List<Mapping> mappings = getExactMappings(pathSegments);
        if (mappings.isEmpty()) {
            mappings = getBlurredMappings(pathSegments);
        }

        HttpMethod method = request.getMethod();
        Mapping mapping = MappingAdapter.findMappingByMethod(mappings, method);

        if (method.equals(HttpMethod.OPTIONS) && mapping == null) {
            return new OptionsHandler(request, mappings, getMappingMap());
        }

        if (mapping == null) {
            return null;
        }

        Mime mime = mapping.getProduce();
        if (mime != null) {
            List<Mime.Rule> produces = mime.getRuleList();
            MediaType mediaType = null;
            for (Mime.Rule produce: produces) {
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

    private List<Mapping> getExactMappings(List<Path.Segment> httpSegments) {
        List<Mapping> mappings = new ArrayList<>();

        Map<Mapping, RequestHandler> mappingMap = getMappingMap();
        for (Mapping mapping: mappingMap.keySet()) {
            Path path = mapping.getPath();
            List<Path.Rule> rules = path.getRuleList();
            for (Path.Rule rule: rules) {
                if (matchExactPath(rule.getSegments(), httpSegments)) {
                    mappings.add(mapping);
                }
            }
        }
        return mappings;
    }

    private boolean matchExactPath(List<Path.Segment> segments, List<Path.Segment> httpSegments) {
        if (httpSegments.size() != segments.size()) {
            return false;
        }

        if (Path.listToPath(segments).equals(Path.listToPath(httpSegments))) {
            return true;
        }
        return false;
    }

    private List<Mapping> getBlurredMappings(List<Path.Segment> httpSegments) {
        List<Mapping> mappings = new ArrayList<>();

        Map<Mapping, RequestHandler> mappingMap = getMappingMap();
        for (Mapping mapping: mappingMap.keySet()) {
            Path path = mapping.getPath();
            List<Path.Rule> rules = path.getRuleList();
            for (Path.Rule rule: rules) {
                if (matchBlurredPath(rule.getSegments(), httpSegments)) {
                    mappings.add(mapping);
                }
            }
        }
        return mappings;
    }

    private boolean matchBlurredPath(List<Path.Segment> segments, List<Path.Segment> httpSegments) {
        if (httpSegments.size() != segments.size()) {
            return false;
        }

        for (int i = 0; i < segments.size(); i++) {
            Path.Segment segment = segments.get(i);
            if (!segment.equals(httpSegments.get(i)) && !segment.isBlurred()) {
                return false;
            }
        }
        return true;
    }

    private void validateParams(Pair param, HttpRequest request) {
        List<Pair.Rule> rules = param.getRuleList();
        for (Pair.Rule rule: rules) {
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
            } else if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)) {
                if (!keys.contains(key) || !values.contains(value)) {
                    throw new ParamValidateException(
                        String.format("The value of parameter %s is missing or wrong.", key));
                }
            } else if (!TextUtils.isEmpty(key) && TextUtils.isEmpty(value)) {
                if (!keys.contains(key)) {
                    throw new ParamValidateException(String.format("The parameter %s is missing.", key));
                }
            }
        }
    }

    private void validateHeaders(Pair header, HttpRequest request) {
        List<Pair.Rule> rules = header.getRuleList();
        for (Pair.Rule rule: rules) {
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
            } else if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value) &&
                (!keys.contains(key) || !values.contains(value))) {
                throw new HeaderValidateException(String.format("The value of header %s is missing or wrong.", key));
            } else if (!TextUtils.isEmpty(key) && TextUtils.isEmpty(value)) {
                if (!keys.contains(key)) {
                    throw new HeaderValidateException(String.format("The header %s is missing.", key));
                }
            }
        }
    }

    private void validateConsume(Mime mime, HttpRequest request) {
        List<Mime.Rule> rules = mime.getRuleList();
        MediaType contentType = request.getContentType();

        List<MediaType> includeType = new ArrayList<>();
        for (Mime.Rule rule: rules) {
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
                includeType.add(consume);
            }
        }

        boolean included = false;
        for (MediaType mediaType: includeType) {
            if (mediaType.includes(contentType)) {
                included = true;
                break;
            }
        }
        if (!included) {
            throw new ContentNotSupportedException(contentType);
        }
    }

    private void validateProduce(Mime mime, HttpRequest request) {
        List<Mime.Rule> rules = mime.getRuleList();
        List<MediaType> accepts = request.getAccepts();
        for (Mime.Rule rule: rules) {
            String type = rule.getType();
            boolean nonContent = type.startsWith("!");
            if (nonContent) {
                type = type.substring(1);
            }
            MediaType produce = new MediaType(type, rule.getSubtype());

            boolean exclude = false;
            for (MediaType accept: accepts) {
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

    public static List<HttpMethod> findSupportMethods(List<Mapping> mappings) {
        List<HttpMethod> methods = new ArrayList<>();
        for (Mapping child: mappings) {
            methods.addAll(child.getMethod().getRuleList());
        }
        return methods;
    }

    public static Mapping findMappingByMethod(List<Mapping> mappings, HttpMethod method) {
        for (Mapping child: mappings) {
            if (child.getMethod().getRuleList().contains(method)) {
                return child;
            }
        }
        return null;
    }
}