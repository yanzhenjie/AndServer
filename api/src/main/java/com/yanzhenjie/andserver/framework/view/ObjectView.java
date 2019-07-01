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
package com.yanzhenjie.andserver.framework.view;

import androidx.annotation.Nullable;

/**
 * Created by Zhenjie Yan on 2018/9/9.
 */
public class ObjectView implements View {

    private final boolean isRest;
    private final Object output;

    public ObjectView(boolean isRest, Object output) {
        this.isRest = isRest;
        this.output = output;
    }

    @Override
    public boolean rest() {
        return isRest;
    }

    @Nullable
    @Override
    public Object output() {
        return output;
    }
}