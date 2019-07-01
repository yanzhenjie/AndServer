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
 * Created by Zhenjie Yan on 2018/9/9.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.SOURCE)
public @interface Addition {

    /**
     * Alias for {@link #stringType()}.
     */
    String[] value() default {};

    /**
     * The added value of the String type.
     */
    String[] stringType() default {};

    /**
     * The added value of the boolean type.
     */
    boolean[] booleanType() default {};

    /**
     * The added value of the int type.
     */
    int[] intTypeType() default {};

    /**
     * The added value of the long type.
     */
    long[] longType() default {};

    /**
     * The added value of the short type.
     */
    short[] shortType() default {};

    /**
     * The added value of the float type.
     */
    float[] floatType() default {};

    /**
     * The added value of the double type.
     */
    double[] doubleType() default {};

    /**
     * The added value of the byte type.
     */
    byte[] byteType() default {};

    /**
     * The added value of the char type.
     */
    char[] charType() default {};
}