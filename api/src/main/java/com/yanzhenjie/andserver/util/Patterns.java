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

/**
 * Created by Zhenjie Yan on 2018/9/5.
 */
public interface Patterns {

    String WORD = "[a-zA-Z0-9_\\-\\.]%s";

    String PATH_0 = String.format(WORD, "*");
    String PATH_1 = String.format(WORD, "+");
    String PATH = String.format("((/%s)|((/%s)+))|((/%s)+/)", PATH_0, PATH_1, PATH_1);

    String PAIR_KEY = String.format(WORD, "+");
    String PAIR_VALUE = "(.)*";
    String PAIR_KEY_VALUE = String.format("(%s)(=)(%s)", PAIR_KEY, PAIR_VALUE);
    String PAIR_NO_KEY = String.format("!%s", PAIR_KEY);
    String PAIR_NO_VALUE = String.format("(%s)(!=)(%s)", PAIR_KEY, PATH_1);

    String FORWARD = "forward:(.)*";
    String REDIRECT = "redirect:(.)*";
}