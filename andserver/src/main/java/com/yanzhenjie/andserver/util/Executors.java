/*
 * Copyright © Yan Zhenjie. All Rights Reserved
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

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;

/**
 * <p>Thread executor.</p>
 * Created by Yan Zhenjie on 2016/6/13.
 */
public class Executors {

    private static Executors instance;

    /**
     * Get instance.
     *
     * @return {@link Executors}.
     */
    public static Executors getInstance() {
        if (instance == null)
            synchronized (Executors.class) {
                if (instance == null)
                    instance = new Executors();
            }
        return instance;
    }

    /**
     * Executor Service.
     */
    private final ExecutorService mService;

    /**
     * Handler.
     */
    private static Handler mHandler;

    private Executors() {
        mService = java.util.concurrent.Executors.newCachedThreadPool();
        mHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * Execute a runnable.
     *
     * @param command {@link Runnable}.
     */
    public void executorService(Runnable command) {
        mService.execute(command);
    }

    /**
     * Execute a runnable.
     *
     * @param command {@link Runnable}.
     */
    public void handler(Runnable command) {
        mHandler.post(command);
    }
}
