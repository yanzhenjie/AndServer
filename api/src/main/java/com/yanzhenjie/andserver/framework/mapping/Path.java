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
package com.yanzhenjie.andserver.framework.mapping;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.yanzhenjie.andserver.util.Patterns;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Zhenjie Yan on 2018/6/14.
 */
public class Path implements Patterns {

    private List<Rule> mRuleList = new LinkedList<>();

    public Path() {
    }

    @NonNull
    public List<Rule> getRuleList() {
        return mRuleList;
    }

    public void addRule(@NonNull String ruleText) {
        Rule rule = new Rule();
        rule.setSegments(pathToList(ruleText));
        mRuleList.add(rule);
    }

    public static class Rule {

        private List<Segment> mSegments;

        public Rule() {
        }

        public List<Segment> getSegments() {
            return mSegments;
        }

        public void setSegments(List<Segment> segments) {
            mSegments = segments;
        }
    }

    public static class Segment {

        private final String value;
        private final boolean isBlurred;

        public Segment(String value, boolean isBlurred) {
            this.value = value;
            this.isBlurred = isBlurred;
        }

        public String getValue() {
            return value;
        }

        public boolean isBlurred() {
            return isBlurred;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Segment)) {
                return false;
            }

            return value.equals(((Segment) obj).value);
        }

        @Override
        public String toString() {
            return value;
        }
    }

    @NonNull
    public static List<Segment> pathToList(@NonNull String path) {
        List<Segment> segmentList = new LinkedList<>();
        if (!TextUtils.isEmpty(path)) {
            while (path.startsWith("/"))
                path = path.substring(1);
            while (path.endsWith("/"))
                path = path.substring(0, path.length() - 1);
            String[] pathArray = path.split("/");
            for (String segmentText: pathArray) {
                Segment segment = new Segment(segmentText, segmentText.contains("{"));
                segmentList.add(segment);
            }
        }
        return Collections.unmodifiableList(segmentList);
    }

    @NonNull
    public static String listToPath(@NonNull List<Segment> segments) {
        StringBuilder builder = new StringBuilder("");
        if (segments.isEmpty()) {
            builder.append("/");
        }
        for (Segment segment: segments) {
            builder.append("/").append(segment.getValue());
        }
        return builder.toString();
    }

    public static boolean matches(@NonNull String path1, @NonNull String path2) {
        if (path1.equals(path2)) {
            return true;
        }

        List<Segment> segments1 = pathToList(path1);
        List<Segment> segments2 = pathToList(path2);

        if (segments1.size() != segments2.size()) {
            return false;
        }

        boolean matches = true;
        for (int i = 0; i < segments1.size(); i++) {
            Segment segment = segments1.get(i);
            if (!segment.equals(segments2.get(i)) && !segment.isBlurred()) {
                matches = false;
                break;
            }
        }
        return matches;
    }
}