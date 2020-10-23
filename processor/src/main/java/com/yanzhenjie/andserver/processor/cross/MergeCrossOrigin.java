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

import com.yanzhenjie.andserver.processor.util.Utils;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by Zhenjie Yan on 10/18/20.
 */
public class MergeCrossOrigin {

    private CrossOriginImpl mParent;
    private CrossOriginImpl mChild;

    private String[] mOrigins;
    private String[] mAllowedHeaders;
    private String[] mExposedHeaders;
    private String[] mMethods;

    public MergeCrossOrigin(CrossOriginImpl parent, CrossOriginImpl child) {
        this.mParent = parent;
        this.mChild = child;
    }

    public String[] value() {
        return origins();
    }

    public String[] origins() {
        if (mOrigins != null) {
            return mOrigins;
        }

        String[] pOrigins = mParent.origins();
        if (ArrayUtils.isEmpty(pOrigins)) {
            pOrigins = mParent.value();
        }
        String[] cOrigins = mChild.origins();
        if (ArrayUtils.isEmpty(cOrigins)) {
            cOrigins = mChild.value();
        }

        mOrigins = Utils.mergeRepeat(pOrigins, cOrigins, true);
        return mOrigins;
    }

    public String[] allowedHeaders() {
        if (mAllowedHeaders != null) {
            return mAllowedHeaders;
        }

        String[] pHeaders = mParent.allowedHeaders();
        String[] cHeaders = mChild.allowedHeaders();
        mAllowedHeaders = Utils.mergeRepeat(pHeaders, cHeaders, true);
        return mAllowedHeaders;
    }

    public String[] exposedHeaders() {
        if (mExposedHeaders != null) {
            return mExposedHeaders;
        }

        String[] pHeaders = mParent.exposedHeaders();
        String[] cHeaders = mChild.exposedHeaders();
        mExposedHeaders = Utils.mergeRepeat(pHeaders, cHeaders, true);
        return mExposedHeaders;
    }

    public String[] methods() {
        if (mMethods != null) {
            return mMethods;
        }

        String[] pMethods = mParent.methods();
        String[] cMethods = mChild.methods();
        mMethods = Utils.mergeRepeat(pMethods, cMethods, true);
        return mMethods;
    }

    public boolean allowCredentials() {
        String cCredentials = mChild.allowCredentials();
        if (StringUtils.isNotEmpty(cCredentials)) {
            return Boolean.parseBoolean(cCredentials);
        }

        String pCredentials = mChild.allowCredentials();
        if (StringUtils.isNotEmpty(pCredentials)) {
            return Boolean.parseBoolean(pCredentials);
        }

        return true;
    }

    public long maxAge() {
        long cMaxAge = mChild.maxAge();
        if (cMaxAge >= 0) {
            return cMaxAge;
        }

        long pMaxAge = mParent.maxAge();
        if (pMaxAge >= 0) {
            return pMaxAge;
        }

        return 1800;
    }

}