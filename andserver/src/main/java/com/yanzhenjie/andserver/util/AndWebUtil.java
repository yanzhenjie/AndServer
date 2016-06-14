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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created on 2016/6/13.
 *
 * @author Yan Zhenjie.
 */
public class AndWebUtil {

    /**
     * 单线程。
     */
    private final static ExecutorService EXECUTORSERVICE = Executors.newCachedThreadPool();

    /**
     * 执行。
     *
     * @param command {@link Runnable}.
     */
    public static void executeRunnable(Runnable command) {
        EXECUTORSERVICE.execute(command);
    }
}
