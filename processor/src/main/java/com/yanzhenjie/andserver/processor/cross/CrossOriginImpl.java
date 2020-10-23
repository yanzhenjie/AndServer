/*
 * Copyright 2020 Zhenjie Yan.
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
package com.yanzhenjie.andserver.processor.cross;

import com.yanzhenjie.andserver.annotation.CrossOrigin;
import com.yanzhenjie.andserver.annotation.RequestMethod;

import org.apache.commons.lang3.ArrayUtils;

/**
 * Created by Zhenjie Yan on 10/19/20.
 */
public class CrossOriginImpl {

    private CrossOrigin mCrossOrigin;

    public CrossOriginImpl(CrossOrigin crossOrigin) {
        this.mCrossOrigin = crossOrigin;
    }

    public String[] value() {
        if (mCrossOrigin != null) {
            return mCrossOrigin.value();
        }
        return new String[0];
    }

    public String[] origins() {
        if (mCrossOrigin != null) {
            return mCrossOrigin.origins();
        }
        return new String[0];
    }

    public String[] allowedHeaders() {
        if (mCrossOrigin != null) {
            return mCrossOrigin.allowedHeaders();
        }
        return new String[0];
    }

    public String[] exposedHeaders() {
        if (mCrossOrigin != null) {
            return mCrossOrigin.exposedHeaders();
        }
        return new String[0];
    }

    public String[] methods() {
        if (mCrossOrigin != null) {
            RequestMethod[] methods = mCrossOrigin.methods();
            String[] textMethods = new String[methods.length];
            if (!ArrayUtils.isEmpty(methods)) {
                for (int i = 0; i < textMethods.length; i++) {
                    textMethods[i] = methods[i].value();
                }
            }
            return textMethods;
        }
        return new String[0];
    }

    public String allowCredentials() {
        if (mCrossOrigin != null) {
            return mCrossOrigin.allowCredentials();
        }
        return "";
    }

    public long maxAge() {
        if (mCrossOrigin != null) {
            return mCrossOrigin.maxAge();
        }
        return -1;
    }
}