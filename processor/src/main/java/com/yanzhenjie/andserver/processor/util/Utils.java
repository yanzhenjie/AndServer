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
import java.util.Locale;
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

    public static void parseMimeType(String rawdata) {
        String primaryType;
        String subType;

        int slashIndex = rawdata.indexOf(47);
        int semIndex = rawdata.indexOf(59);
        if (slashIndex < 0 && semIndex < 0) {
            throw new RuntimeException("Unable to find a sub type.");
        } else if (slashIndex < 0 && semIndex >= 0) {
            throw new RuntimeException("Unable to find a sub type.");
        } else {
            if (slashIndex >= 0 && semIndex < 0) {
                primaryType = rawdata.substring(0, slashIndex).trim().toLowerCase(Locale.ENGLISH);
                subType = rawdata.substring(slashIndex + 1).trim().toLowerCase(Locale.ENGLISH);
            } else {
                if (slashIndex >= semIndex) {
                    throw new RuntimeException("Unable to find a sub type.");
                }

                primaryType = rawdata.substring(0, slashIndex).trim().toLowerCase(Locale.ENGLISH);
                subType = rawdata.substring(slashIndex + 1, semIndex).trim().toLowerCase(Locale.ENGLISH);
            }

            if (!isValidToken(primaryType)) {
                throw new RuntimeException("Primary type is invalid.");
            } else if (!isValidToken(subType)) {
                throw new RuntimeException("Sub type is invalid.");
            }
        }
    }

    private static boolean isValidToken(String s) {
        int len = s.length();
        if (len > 0) {
            for (int i = 0; i < len; ++i) {
                char c = s.charAt(i);
                if (!isTokenChar(c)) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    private static boolean isTokenChar(char c) {
        return c > ' ' && c < 127 && "()<>@,;:/[]?=\\\"".indexOf(c) < 0;
    }

}