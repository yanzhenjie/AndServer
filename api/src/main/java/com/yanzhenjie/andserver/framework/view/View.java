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
package com.yanzhenjie.andserver.framework.view;

import androidx.annotation.Nullable;

/**
 * Created by Zhenjie Yan on 2018/8/29.
 */
public interface View {

    /**
     * Is it a rest style view?
     *
     * @return true, otherwise is false.
     */
    boolean rest();

    /**
     * Get the output.
     *
     * @return output, e.g. {@code "redirect:/user/list"}, {@code "forward:/user/list"}, {@code "/user/list"}, String,
     *     JSONObject, Object, Basic data type(int, short, long, double, float, byte, boolean char).
     */
    @Nullable
    Object output();
}