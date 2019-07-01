/*
 * Copyright © 2019 Zhenjie Yan.
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
package com.yanzhenjie.andserver.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Zhenjie Yan on 2018/9/9.
 * <pre>
 * <code>@Config</code>
 * public class AppConfig implements WebConfig {
 *
 *     <code>@Override</code>
 *     public void onConfig(Context context, Delegate delegate) {
 *         Website website = ...;
 *         delegate.addWebsite(website);
 *
 *         Multipart multipart = Multipart.newBuilder()...build();
 *         delegate.setMultipart(multipart);
 *     }
 * }
 * </pre>
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
public @interface Config {

    /**
     * Group name.
     */
    String value() default "default";
}