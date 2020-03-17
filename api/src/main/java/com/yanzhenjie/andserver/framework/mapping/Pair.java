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

import com.yanzhenjie.andserver.util.Patterns;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Zhenjie Yan on 2018/6/14.
 */
public class Pair implements Patterns {

    private List<Rule> mRuleList = new LinkedList<>();

    public Pair() {
    }

    @NonNull
    public List<Rule> getRuleList() {
        return mRuleList;
    }

    public void addRule(@NonNull String ruleText) {
        if (ruleText.matches(PAIR_NO_VALUE)) {
            String[] keyValue = ruleText.split("=");
            Rule rule = new Rule();
            String key = keyValue[0];
            rule.setKey(key.substring(0, key.length() - 1));
            rule.setValue(keyValue[1]);
            rule.setNoValue(true);
            mRuleList.add(rule);
        } else if (ruleText.matches(PAIR_KEY_VALUE)) {
            String[] keyValue = ruleText.split("=");

            Rule rule = new Rule();
            rule.setKey(keyValue[0]);
            rule.setValue(keyValue[1]);
            mRuleList.add(rule);
        } else if (ruleText.matches(PAIR_NO_KEY)) {
            Rule rule = new Rule();
            rule.setKey(ruleText.substring(1));
            rule.setNoKey(true);
            mRuleList.add(rule);
        } else if (ruleText.matches(PAIR_KEY)) {
            Rule rule = new Rule();
            rule.setKey(ruleText);
            mRuleList.add(rule);
        }
    }

    public static class Rule {

        private String key;
        private String value;
        private boolean noKey;
        private boolean noValue;

        public Rule() {
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public boolean isNoKey() {
            return noKey;
        }

        public void setNoKey(boolean noKey) {
            this.noKey = noKey;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public void setNoValue(boolean noValue) {
            this.noValue = noValue;
        }
    }
}