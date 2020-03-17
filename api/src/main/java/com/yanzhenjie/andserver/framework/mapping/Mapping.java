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
package com.yanzhenjie.andserver.framework.mapping;

/**
 * <p> Save the request mapping configuration. </p>
 *
 * Created by Zhenjie Yan on 2018/6/13.
 */
public class Mapping {

    private Path mPath;
    private Method mMethod;
    private Pair mParam;
    private Pair mHeader;
    private Mime mConsume;
    private Mime mProduce;

    public Mapping() {
    }

    public Path getPath() {
        return mPath;
    }

    public void setPath(Path path) {
        mPath = path;
    }

    public Method getMethod() {
        return mMethod;
    }

    public void setMethod(Method method) {
        mMethod = method;
    }

    public Pair getParam() {
        return mParam;
    }

    public void setParam(Pair param) {
        mParam = param;
    }

    public Pair getHeader() {
        return mHeader;
    }

    public void setHeader(Pair header) {
        mHeader = header;
    }

    public Mime getConsume() {
        return mConsume;
    }

    public void setConsume(Mime consume) {
        mConsume = consume;
    }

    public Mime getProduce() {
        return mProduce;
    }

    public void setProduce(Mime produce) {
        mProduce = produce;
    }
}