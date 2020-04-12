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
package com.yanzhenjie.andserver.plugin.util;

import org.gradle.api.Project;

/**
 * Created by Zhenjie Yan on 4/10/20.
 */
public class Log {

    private static org.gradle.api.logging.Logger sLogger;

    public static void inject(Project project) {
        sLogger = project.getLogger();
    }

    public Log() {
    }

    public void i(String format, Object... args) {
        if (null != format && null != sLogger) {
            sLogger.info("AndServer::Info >>> " + String.format(format, args));
        }
    }

    public void d(String format, Object... args) {
        if (null != format && null != sLogger) {
            sLogger.debug("AndServer::Debug >>> " + String.format(format, args));
        }
    }

    public void w(String format, Object... args) {
        if (null != format && null != sLogger) {
            sLogger.warn("AndServer::Warn >>> " + String.format(format, args));
        }
    }

    public void e(String format, Object... args) {
        if (null != format && null != sLogger) {
            sLogger.error("AndServer::Error >>> " + String.format(format, args));
        }
    }

    public void e(Throwable error) {
        if (null != error) {
            sLogger.error("AndServer::Error >>> " + formatStackTrace(error.getStackTrace()));
        }
    }

    private static String formatStackTrace(StackTraceElement[] stackTrace) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : stackTrace) {
            sb.append("    at ").append(element.toString());
            sb.append("\n");
        }
        return sb.toString();
    }

}