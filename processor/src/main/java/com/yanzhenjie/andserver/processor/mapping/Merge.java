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
package com.yanzhenjie.andserver.processor.mapping;

import com.yanzhenjie.andserver.annotation.RequestMethod;

import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by YanZhenjie on 2018/9/8.
 */
public class Merge implements Mapping {

    private String[] mPaths;
    private String[] mMethods;
    private String[] mParams;
    private String[] mHeaders;
    private String[] mConsumes;
    private String[] mProduces;

    private Mapping mParent;
    private Mapping mChild;

    public Merge(Mapping parent, Mapping child) {
        this.mParent = parent;
        this.mChild = child;
    }

    @Override
    public String[] value() {
        return path();
    }

    @Override
    public String[] path() {
        if (mPaths != null) return mPaths;

        String[] pPaths = mParent.path();
        if (ArrayUtils.isEmpty(pPaths)) {
            pPaths = mParent.value();
        }
        String[] cPaths = mChild.path();
        if (ArrayUtils.isEmpty(cPaths)) {
            cPaths = mChild.value();
        }

        if (ArrayUtils.isNotEmpty(pPaths)) {
            List<String> paths = new ArrayList<>();
            for (String pPath : pPaths) {
                for (String cPath : cPaths) {
                    String path = pPath + cPath;
                    paths.add(path);
                }
            }
            mPaths = paths.toArray(new String[0]);
        } else {
            mPaths = cPaths;
        }

        mPaths = mergeRepeat(mPaths, null, false);
        return mPaths;
    }

    @Override
    public String[] method() {
        if (mMethods != null) return mMethods;

        String[] pMethods = mParent.method();
        String[] cMethods = mChild.method();
        mMethods = mergeRepeat(pMethods, cMethods, true);
        if (ArrayUtils.isEmpty(mMethods)) {
            mMethods = new String[] {RequestMethod.GET.value()};
        }
        return mMethods;
    }

    @Override
    public String[] params() {
        if (mParams != null) return mParams;

        String[] pParams = mParent.params();
        String[] cParams = mChild.params();
        mParams = mergeRepeat(pParams, cParams, false);
        return mParams;
    }

    @Override
    public String[] headers() {
        if (mHeaders != null) return mHeaders;

        String[] pHeaders = mParent.headers();
        String[] cHeaders = mChild.headers();
        mHeaders = mergeRepeat(pHeaders, cHeaders, true);
        return mHeaders;
    }

    @Override
    public String[] consumes() {
        if (mConsumes != null) return mConsumes;

        String[] pConsumes = mParent.consumes();
        String[] cConsumes = mChild.consumes();
        mConsumes = mergeRepeat(pConsumes, cConsumes, true);
        return mConsumes;
    }

    @Override
    public String[] produces() {
        if (mProduces != null) return mProduces;

        String[] pProduces = mParent.produces();
        String[] cProduces = mChild.produces();
        mProduces = mergeRepeat(pProduces, cProduces, true);
        return mProduces;
    }

    @Override
    public boolean isRest() {
        return mParent.isRest() || mChild.isRest();
    }

    private static String[] mergeRepeat(String[] parents, String[] children, boolean ignoreCap) {
        Map<String, String> map = new HashMap<>();
        if (ArrayUtils.isNotEmpty(parents)) {
            for (String parent : parents) {
                map.put(ignoreCap ? parent.toLowerCase() : parent, parent);
            }
        }
        if (ArrayUtils.isNotEmpty(children)) {
            for (String child : children) {
                map.put(ignoreCap ? child.toLowerCase() : child, child);
            }
        }
        Collection<String> values = map.values();
        return values.toArray(new String[0]);
    }
}