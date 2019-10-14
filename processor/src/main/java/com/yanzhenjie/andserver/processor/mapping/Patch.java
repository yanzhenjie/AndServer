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
package com.yanzhenjie.andserver.processor.mapping;

import com.yanzhenjie.andserver.annotation.PatchMapping;
import com.yanzhenjie.andserver.annotation.RequestMethod;

/**
 * Created by Zhenjie Yan on 2018/6/16.
 */
public class Patch implements Mapping {

    private PatchMapping mMapping;
    private boolean isRest;

    public Patch(PatchMapping mapping, boolean rest) {
        this.mMapping = mapping;
        this.isRest = rest;
    }

    @Override
    public String[] value() {
        return mMapping.value();
    }

    @Override
    public String[] path() {
        return mMapping.path();
    }

    @Override
    public String[] method() {
        return new String[]{RequestMethod.PATCH.value()};
    }

    @Override
    public String[] params() {
        return mMapping.params();
    }

    @Override
    public String[] headers() {
        return mMapping.headers();
    }

    @Override
    public String[] consumes() {
        return mMapping.consumes();
    }

    @Override
    public String[] produces() {
        return mMapping.produces();
    }

    @Override
    public boolean isRest() {
        return isRest;
    }
}