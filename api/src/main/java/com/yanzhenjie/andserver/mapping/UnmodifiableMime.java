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

import android.support.annotation.NonNull;

import java.util.Collections;
import java.util.List;

/**
 * Created by YanZhenjie on 2018/9/9.
 */
public class UnmodifiableMime extends Mime {

    private Mime mMime;

    public UnmodifiableMime(Mime mime) {
        this.mMime = mime;
    }

    @NonNull
    @Override
    public List<Rule> getRuleList() {
        return Collections.unmodifiableList(mMime.getRuleList());
    }

    @Override
    public void addRule(@NonNull String ruleText) {
        throw new UnsupportedOperationException();
    }
}