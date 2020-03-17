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

import androidx.annotation.NonNull;

import com.yanzhenjie.andserver.util.MediaType;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Zhenjie Yan on 2018/6/14.
 */
public class Mime {

    private List<Rule> mRuleList = new LinkedList<>();

    public Mime() {
    }

    @NonNull
    public List<Rule> getRuleList() {
        return mRuleList;
    }

    public void addRule(@NonNull String ruleText) {
        MediaType mimeType = MediaType.valueOf(ruleText);
        Rule rule = new Rule(mimeType.getType(), mimeType.getSubtype(), mimeType.getParameters());
        mRuleList.add(rule);
    }

    public static class Rule extends MediaType {

        public Rule(String type, String subtype, Map<String, String> parameters) {
            super(type, subtype, parameters);
        }
    }
}