/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.yanzhenjie.andserver.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Utility class to generate HTTP dates.
 *
 * @author Remy Maucherat
 */
public final class HttpDateFormat {

    /**
     * The date format of the Http header.
     */
    private static final String RFC1123_DATE = "EEE, dd MMM yyyy HH:mm:ss zzz";
    private static final SimpleDateFormat[] FORMATS_TEMPLATE = {new SimpleDateFormat(RFC1123_DATE, Locale.US),
        new SimpleDateFormat("EEEEEE, dd-MMM-yy HH:mm:ss zzz", Locale.US),
        new SimpleDateFormat("EEE MMMM d HH:mm:ss yyyy", Locale.US)};

    private static final TimeZone GMT_ZONE = TimeZone.getTimeZone("GMT");
    private static final SimpleDateFormat FORMAT = new SimpleDateFormat(RFC1123_DATE, Locale.US);

    static {
        FORMAT.setTimeZone(GMT_ZONE);
    }

    /**
     * Get the current date in HTTP format.
     *
     * @return the HTTP date.
     */
    public static String getCurrentDate() {
        synchronized (FORMAT) {
            long now = System.currentTimeMillis();
            return FORMAT.format(new Date(now));
        }
    }

    /**
     * Get the HTTP format of the specified date.
     *
     * @param value the date.
     *
     * @return the HTTP date.
     */
    public static String formatDate(long value) {
        synchronized (HttpDateFormat.class) {
            Date dateValue = new Date(value);
            return FORMAT.format(dateValue);
        }
    }

    /**
     * Try to parse the given date as a HTTP date.
     *
     * @param value the HTTP date.
     *
     * @return the date as a long.
     */
    public static long parseDate(String value) {
        Date date = null;
        for (SimpleDateFormat format : FORMATS_TEMPLATE) {
            try {
                date = format.parse(value);
            } catch (ParseException e) {
                // Nothing.
            }
        }

        if (date == null) {
            return -1L;
        }
        return date.getTime();
    }
}