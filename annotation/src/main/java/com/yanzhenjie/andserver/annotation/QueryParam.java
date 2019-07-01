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
package com.yanzhenjie.andserver.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Zhenjie Yan on 2018/9/13.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.SOURCE)
public @interface QueryParam {

    /**
     * Alias for {@link #name()}.
     */
    String value() default "";

    /**
     * The name of the request parameter to bind to.
     */
    String name() default "";

    /**
     * Whether the parameter is required.
     *
     * <p>Defaults to {@code true}, leading to an exception being thrown if the parameter is missing in the request.
     *
     * <p>Alternatively, provide a {@link #defaultValue()}, which implicitly sets this flag to {@code false}.
     */
    boolean required() default true;

    /**
     * The default value to use as a fallback.
     *
     * <p>Supplying a default value implicitly sets {@link #required()} to {@code false}.
     */
    String defaultValue() default "";
}