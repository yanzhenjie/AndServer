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
package com.yanzhenjie.andserver.http;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Created by Zhenjie Yan on 2018/8/7.
 */
public class AcceptLanguage {

    private final Locale locale;
    private final double quality;

    protected AcceptLanguage(Locale locale, double quality) {
        this.locale = locale;
        this.quality = quality;
    }

    public Locale getLocale() {
        return locale;
    }

    public double getQuality() {
        return quality;
    }

    public static List<AcceptLanguage> parse(String input) {
        if (TextUtils.isEmpty(input)) {
            return Collections.emptyList();
        }

        String[] segments = input.split(",");
        if (segments.length == 0) {
            return Collections.emptyList();
        }

        List<AcceptLanguage> list = new ArrayList<>();
        for (String segment: segments) {
            String[] values = segment.split(";");
            if (values.length == 2 && values[1].length() > 2 && values[1].charAt(0) == 'q' &&
                values[1].charAt(1) == '=') {
                String q = values[1].substring(2);
                try {
                    list.add(new AcceptLanguage(new Locale(values[1]), Double.parseDouble(q)));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }

        return list;
    }
}