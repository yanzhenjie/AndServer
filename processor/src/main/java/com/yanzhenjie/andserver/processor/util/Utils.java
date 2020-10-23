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
package com.yanzhenjie.andserver.processor.util;

import org.apache.commons.lang3.ArrayUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Zhenjie Yan on 10/19/20.
 */
public class Utils {

    public static String[] mergeRepeat(String[] parents, String[] children, boolean ignoreCap) {
        Map<String, String> map = new HashMap<>();
        if (ArrayUtils.isNotEmpty(parents)) {
            for (String parent: parents) {
                map.put(ignoreCap ? parent.toLowerCase() : parent, parent);
            }
        }
        if (ArrayUtils.isNotEmpty(children)) {
            for (String child: children) {
                map.put(ignoreCap ? child.toLowerCase() : child, child);
            }
        }
        Collection<String> values = map.values();
        return values.toArray(new String[0]);
    }

}