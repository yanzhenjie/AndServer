/*
 * Copyright © 2018 Zhenjie Yan.
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
package com.yanzhenjie.andserver.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by Zhenjie Yan on 2018/9/11.
 */
public abstract class TypeWrapper<T> {

    private final Type mType;

    public TypeWrapper() {
        Type superClass = getClass().getGenericSuperclass();
        mType = ((ParameterizedType) superClass).getActualTypeArguments()[0];
    }

    public Type getType() {
        return mType;
    }
}