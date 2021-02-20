/*
 * Copyright 2018 Zhenjie Yan
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
package com.yanzhenjie.andserver;

import android.content.Context;
import android.content.res.AssetManager;

import com.yanzhenjie.andserver.register.OnRegister;
import com.yanzhenjie.andserver.register.Register;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zhenjie Yan on 2018/9/23.
 */
public class ComponentRegister {

    private static final String ANDSERVER_REGISTER_SUFFIX = ".andserver";
    private static final String PROCESSOR_PACKAGE = ".andserver.processor.generator.";
    private static final List<String> REGISTER_LIST = new ArrayList<>();

    static {
        REGISTER_LIST.add("AdapterRegister");
        REGISTER_LIST.add("ConfigRegister");
        REGISTER_LIST.add("ConverterRegister");
        REGISTER_LIST.add("InterceptorRegister");
        REGISTER_LIST.add("ResolverRegister");
    }

    private Context mContext;

    public ComponentRegister(Context context) {
        this.mContext = context;
    }

    public void register(Register register, String group)
        throws InstantiationException, IllegalAccessException {
        AssetManager manager = mContext.getAssets();
        String[] pathList = null;
        try {
            pathList = manager.list("");
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (pathList == null || pathList.length == 0) {
            return;
        }

        for (String path: pathList) {
            if (path.endsWith(ANDSERVER_REGISTER_SUFFIX)) {
                String packageName = path.substring(0, path.lastIndexOf(ANDSERVER_REGISTER_SUFFIX));
                for (String clazz: REGISTER_LIST) {
                    String className = String.format("%s%s%s", packageName, PROCESSOR_PACKAGE, clazz);
                    registerClass(register, group, className);
                }
            }
        }
    }

    private void registerClass(Register register, String group, String className)
        throws InstantiationException, IllegalAccessException {
        try {
            Class<?> clazz = Class.forName(className);
            if (OnRegister.class.isAssignableFrom(clazz)) {
                OnRegister load = (OnRegister) clazz.newInstance();
                load.onRegister(mContext, group, register);
            }
        } catch (ClassNotFoundException ignored) {
        }
    }
}