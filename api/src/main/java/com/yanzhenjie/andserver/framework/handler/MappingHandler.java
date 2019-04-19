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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yanzhenjie.andserver.framework.ETag;
import com.yanzhenjie.andserver.framework.LastModified;
import com.yanzhenjie.andserver.http.HttpRequest;
import com.yanzhenjie.andserver.mapping.Addition;
import com.yanzhenjie.andserver.mapping.Mapping;
import com.yanzhenjie.andserver.mapping.Path;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by YanZhenjie on 2018/9/9.
 */
public abstract class MappingHandler implements MethodHandler {

    private final Object mHost;
    private final Mapping mMapping;
    private final Addition mAddition;
    private final boolean isRest;

    public MappingHandler(@NonNull Object host, @NonNull Mapping mapping, @NonNull Addition addition, boolean isRest) {
        this.mHost = host;
        this.mMapping = mapping;
        this.mAddition = addition;
        this.isRest = isRest;
    }

    @Override
    public String getETag(@NonNull HttpRequest request) throws IOException {
        Object o = getHost();
        if (o instanceof ETag) {
            return ((ETag)o).getETag(request);
        }
        return null;
    }

    @Override
    public long getLastModified(@NonNull HttpRequest request) throws IOException {
        Object o = getHost();
        if (o instanceof LastModified) {
            return ((LastModified)o).getLastModified(request);
        }
        return -1;
    }

    @Override
    public boolean isRest() {
        return isRest;
    }

    @Nullable
    @Override
    public Addition getAddition() {
        return mAddition;
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
        for (Path.Rule rule : ruleList) {
            List<Path.Segment> segments = rule.getSegments();
            if (httpSegments.size() != httpSegments.size()) continue;

            String path = Path.listToPath(segments);
            if (path.equals(httpPath)) return Collections.emptyMap();

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

            if (matches) {
                if (isBlurred) {
                    Map<String, String> map = new HashMap<>();
                    for (int i = 0; i < segments.size(); i++) {
                        Path.Segment segment = segments.get(i);
                        Path.Segment httpSegment = httpSegments.get(i);

                        String key = segment.getValue();
                        key = key.substring(1, key.length() - 1);
                        map.put(key, httpSegment.getValue());
                    }
                    return map;
                }

                return Collections.emptyMap();
            }
        }

        return Collections.emptyMap();
    }
}