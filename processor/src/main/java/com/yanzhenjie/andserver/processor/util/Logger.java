/*
 * Copyright © Zhenjie Yan
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

import org.apache.commons.lang3.StringUtils;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;

/**
 * Created by Zhenjie Yan on 2018/2/5.
 */
public class Logger {

    private Messager mMessager;

    public Logger(Messager messager) {
        this.mMessager = messager;
    }

    public void i(CharSequence info) {
        if (StringUtils.isNotEmpty(info)) {
            mMessager.printMessage(Diagnostic.Kind.NOTE, info);
        }
    }

    public void e(CharSequence error) {
        if (StringUtils.isNotEmpty(error)) {
            mMessager.printMessage(Diagnostic.Kind.ERROR, "An exception is encountered, " + error);
        }
    }

    public void e(Throwable error) {
        if (null != error) {
            mMessager.printMessage(Diagnostic.Kind.ERROR,
                "An exception is encountered, " + error.getMessage() + "\n" + formatStackTrace(error.getStackTrace()));
        }
    }

    public void w(CharSequence warning) {
        if (StringUtils.isNotEmpty(warning)) {
            mMessager.printMessage(Diagnostic.Kind.WARNING, warning);
        }
    }

    private String formatStackTrace(StackTraceElement[] stackTrace) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : stackTrace) {
            sb.append("    at ").append(element.toString());
            sb.append("\n");
        }
        return sb.toString();
    }

}