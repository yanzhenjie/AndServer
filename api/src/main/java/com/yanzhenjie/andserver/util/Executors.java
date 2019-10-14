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

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Created by Zhenjie Yan on 2018/9/10.
 */
public class Executors {

    private static Executors instance;

    /**
     * Get instance.
     *
     * @return {@link Executors}.
     */
    public static Executors getInstance() {
        if (instance == null) {
            synchronized (Executors.class) {
                if (instance == null) {
                    instance = new Executors();
                }
            }
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
     */
    public void execute(Runnable runnable) {
        mService.execute(runnable);
    }

    /**
     * Submit a runnable.
     */
    public Future<?> submit(Runnable runnable) {
        return mService.submit(runnable);
    }

    /**
     * Submit a runnable.
     */
    public <T> Future<T> submit(Runnable runnable, T result) {
        return mService.submit(runnable, result);
    }

    /**
     * Submit a callable.
     */
    public <T> Future<T> submit(Callable<T> callable) {
        return mService.submit(callable);
    }

    /**
     * Post a runnable.
     */
    public void post(Runnable command) {
        mHandler.post(command);
    }

    /**
     * Delay post a runnable.
     */
    public void postDelayed(Runnable command, long delayedMillis) {
        mHandler.postDelayed(command, delayedMillis);
    }
}