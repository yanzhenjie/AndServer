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

import com.yanzhenjie.andserver.mapping.Addition;
import com.yanzhenjie.andserver.mapping.Mapping;

/**
 * Created by YanZhenjie on 2018/6/16.
 */
public interface MethodHandler extends RequestHandler {

    /**
     * Is a rest style handler ?
     *
     * @return true, otherwise is false.
     */
    boolean isRest();

    /**
     * Get addition configuration, addition provides some added value.
     *
     * @return {@link Addition}.
     */
    @Nullable
    Addition getAddition();

    /**
     * Get mapping configuration, mapping provides all the annotation information for this method.
     *
     * @return {@link Mapping}.
     */
    @NonNull
    Mapping getMapping();
}