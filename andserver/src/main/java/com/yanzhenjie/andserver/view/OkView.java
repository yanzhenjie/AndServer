/*
 * Copyright © 2017 Yan Zhenjie.
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
package com.yanzhenjie.andserver.view;

import org.apache.httpcore.HttpEntity;

/**
 * Created by YanZhenjie on 2017/12/22.
 */
public class OkView extends View {

    public OkView() {
        super(200);
    }

    public OkView(String httpBody) {
        super(200, httpBody);
    }

    public OkView(HttpEntity httpEntity) {
        super(200, httpEntity);
    }
}