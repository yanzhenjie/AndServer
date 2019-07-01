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
package com.yanzhenjie.andserver.sample.util;

import android.util.Log;

/**
 * Created by Zhenjie Yan on 2018/9/12.
 */
public class Logger {

    private static final String TAG = "AndServerSample";
    private static final boolean DEBUG = true;

    public static void i(Object obj) {
        if (DEBUG) {
            Log.i(TAG, obj == null ? "null" : obj.toString());
        }
    }

    public static void d(Object obj) {
        if (DEBUG) {
            Log.d(TAG, obj == null ? "null" : obj.toString());
        }
    }

    public static void v(Object obj) {
        if (DEBUG) {
            Log.v(TAG, obj == null ? "null" : obj.toString());
        }
    }

    public static void w(Object obj) {
        if (DEBUG) {
            Log.w(TAG, obj == null ? "null" : obj.toString());
        }
    }

    public static void e(Object obj) {
        if (DEBUG) {
            Log.e(TAG, obj == null ? "null" : obj.toString());
        }
    }
}