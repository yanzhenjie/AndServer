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
package com.yanzhenjie.andserver.mapping;

/**
 * Created by YanZhenjie on 2018/9/9.
 */
public class UnmodifiableMapping extends Mapping {

    private Mapping mMapping;

    public UnmodifiableMapping(Mapping mapping) {
        this.mMapping = mapping;
    }

    @Override
    public Path getPath() {
        Path path = mMapping.getPath();
        if (path != null) return new UnmodifiablePath(path);
        return null;
    }

    @Override
    public void setPath(Path path) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Method getMethod() {
        Method method = mMapping.getMethod();
        if (method != null) return new UnmodifiableMethod(method);
        return null;
    }

    @Override
    public void setMethod(Method method) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Pair getParam() {
        Pair param = mMapping.getParam();
        if (param != null) return new UnmodifiablePair(param);
        return null;
    }

    @Override
    public void setParam(Pair param) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Pair getHeader() {
        Pair header = mMapping.getHeader();
        if (header != null) return new UnmodifiablePair(header);
        return null;
    }

    @Override
    public void setHeader(Pair header) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Mime getConsume() {
        Mime consume = mMapping.getConsume();
        if (consume != null) return new UnmodifiableMime(consume);
        return null;
    }

    @Override
    public void setConsume(Mime consume) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Mime getProduce() {
        Mime produce = mMapping.getProduce();
        if (produce != null) return new UnmodifiableMime(produce);
        return null;
    }

    @Override
    public void setProduce(Mime produce) {
        throw new UnsupportedOperationException();
    }
}