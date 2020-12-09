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

import com.yanzhenjie.andserver.annotation.RequestMethod;
import com.yanzhenjie.andserver.processor.util.Utils;

import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zhenjie Yan on 2018/9/8.
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
        if (mPaths != null) {
            return mPaths;
        }

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
            for (String pPath: pPaths) {
                for (String cPath: cPaths) {
                    String path = pPath + cPath;
                    paths.add(path);

                    if (path.endsWith("/")) {
                        if (path.length() > 1) {
                            String copyPath = path.substring(0, path.length() - 1);
                            paths.add(copyPath);
                        }
                    } else {
                        String copyPath = path + "/";
                        paths.add(copyPath);
                    }
                }
            }
            mPaths = paths.toArray(new String[0]);
        } else {
            if (ArrayUtils.isNotEmpty(cPaths)) {
                List<String> paths = new ArrayList<>();
                for (String cPath: cPaths) {
                    paths.add(cPath);

                    if (cPath.endsWith("/")) {
                        if (cPath.length() > 1) {
                            String copyPath = cPath.substring(0, cPath.length() - 1);
                            paths.add(copyPath);
                        }
                    } else {
                        String copyPath = cPath + "/";
                        paths.add(copyPath);
                    }
                }
                mPaths = paths.toArray(new String[0]);
            } else {
                mPaths = cPaths;
            }
        }

        mPaths = Utils.mergeRepeat(mPaths, null, false);
        return mPaths;
    }

    @Override
    public String[] method() {
        if (mMethods != null) {
            return mMethods;
        }

        String[] pMethods = mParent.method();
        String[] cMethods = mChild.method();
        mMethods = Utils.mergeRepeat(pMethods, cMethods, true);
        if (ArrayUtils.isEmpty(mMethods)) {
            mMethods = new String[] {RequestMethod.GET.value()};
        }
        return mMethods;
    }

    @Override
    public String[] params() {
        if (mParams != null) {
            return mParams;
        }

        String[] pParams = mParent.params();
        String[] cParams = mChild.params();
        mParams = Utils.mergeRepeat(pParams, cParams, false);
        return mParams;
    }

    @Override
    public String[] headers() {
        if (mHeaders != null) {
            return mHeaders;
        }

        String[] pHeaders = mParent.headers();
        String[] cHeaders = mChild.headers();
        mHeaders = Utils.mergeRepeat(pHeaders, cHeaders, true);
        return mHeaders;
    }

    @Override
    public String[] consumes() {
        if (mConsumes != null) {
            return mConsumes;
        }

        String[] pConsumes = mParent.consumes();
        String[] cConsumes = mChild.consumes();
        mConsumes = Utils.mergeRepeat(pConsumes, cConsumes, true);
        return mConsumes;
    }

    @Override
    public String[] produces() {
        if (mProduces != null) {
            return mProduces;
        }

        String[] pProduces = mParent.produces();
        String[] cProduces = mChild.produces();
        mProduces = Utils.mergeRepeat(pProduces, cProduces, true);
        return mProduces;
    }

    @Override
    public boolean isRest() {
        return mParent.isRest() || mChild.isRest();
    }
}