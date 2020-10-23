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
package com.yanzhenjie.andserver.http;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.yanzhenjie.andserver.util.HttpDateFormat.parseDate;

/**
 * Created by Zhenjie Yan on 2018/8/29.
 */
public class Modified implements HttpHeaders {

    private static final Pattern ETAG_PATTERN = Pattern.compile("\\*|\\s*((W\\/)?(\"[^\"]*\"))\\s*,?");

    private HttpRequest mRequest;
    private HttpResponse mResponse;

    private boolean isNotModified;

    public Modified(@NonNull HttpRequest request, @NonNull HttpResponse response) {
        this.mRequest = request;
        this.mResponse = response;
    }

    /**
     * Process {@code Modified} according to given the supplied {@code Last-Modified}.
     *
     * @param lastModified the last-modified timestamp in milliseconds of the resource.
     *
     * @return true if the request does not require further processing.
     */
    public boolean process(long lastModified) {
        return process(null, lastModified);
    }

    /**
     * Process {@code Modified} according to given the supplied {@code ETag}.
     *
     * @param eTag the tag of the resource.
     *
     * @return true if the request does not require further processing.
     */
    public boolean process(String eTag) {
        return process(eTag, -1);
    }

    /**
     * Process {@code Modified} according to given the supplied {@code Last-Modified} and {@code ETag}.
     *
     * @param eTag the tag of the resource.
     * @param lastModified he last-modified timestamp in milliseconds of the resource.
     *
     * @return true if the request does not require further processing.
     */
    public boolean process(@Nullable String eTag, long lastModified) {
        if (isNotModified) {
            return true;
        }

        // See https://tools.ietf.org/html/rfc7232#section-6
        if (validateIfUnmodifiedSince(lastModified)) {
            if (!isNotModified) {
                mResponse.setStatus(StatusCode.SC_LENGTH_REQUIRED);
            }
            return isNotModified;
        }

        // First, prioritized.
        boolean validated = validateIfNoneMatch(eTag);
        // Second.
        if (!validated) {
            validateIfModifiedSince(lastModified);
        }

        // Update response
        HttpMethod method = mRequest.getMethod();
        boolean isGetHead = (method == HttpMethod.GET || method == HttpMethod.HEAD);
        if (isNotModified) {
            mResponse.setStatus(isGetHead ? StatusCode.SC_NOT_MODIFIED : StatusCode.SC_LENGTH_REQUIRED);
        }
        if (isGetHead) {
            if (lastModified > 0 && mResponse.getHeader(LAST_MODIFIED) == null) {
                mResponse.setDateHeader(LAST_MODIFIED, lastModified);
            }
            if (!TextUtils.isEmpty(eTag) && mResponse.getHeader(ETAG) == null) {
                mResponse.setHeader(ETAG, padETagIfNecessary(eTag));
            }
            mResponse.setHeader(CACHE_CONTROL, "private");
        }
        return isNotModified;
    }

    private boolean validateIfNoneMatch(String eTag) {
        if (TextUtils.isEmpty(eTag)) {
            return false;
        }

        List<String> ifNoneMatch = mRequest.getHeaders(IF_NONE_MATCH);
        if (ifNoneMatch.isEmpty()) {
            return false;
        }

        // We will perform this validation...
        eTag = padETagIfNecessary(eTag);
        for (String clientETags: ifNoneMatch) {
            Matcher eTagMatcher = ETAG_PATTERN.matcher(clientETags);
            // Compare weak/strong ETags as per https://tools.ietf.org/html/rfc7232#section-2.3
            while (eTagMatcher.find()) {
                if (!TextUtils.isEmpty(eTagMatcher.group()) &&
                    eTag.replaceFirst("^W/", "").equals(eTagMatcher.group(3))) {
                    isNotModified = true;
                    break;
                }
            }
        }
        return true;
    }

    private String padETagIfNecessary(String eTag) {
        if (TextUtils.isEmpty(eTag)) {
            return eTag;
        }
        if ((eTag.startsWith("\"") || eTag.startsWith("W/\"")) && eTag.endsWith("\"")) {
            return eTag;
        }
        return "\"" + eTag + "\"";
    }

    private boolean validateIfModifiedSince(long lastModifiedTimestamp) {
        if (lastModifiedTimestamp < 0) {
            return false;
        }

        long ifModifiedSince = parseDateHeader(IF_MODIFIED_SINCE);
        if (ifModifiedSince == -1) {
            return false;
        }

        // We will perform this validation...
        isNotModified = ifModifiedSince >= lastModifiedTimestamp;
        return true;
    }

    private boolean validateIfUnmodifiedSince(long lastModifiedTimestamp) {
        if (lastModifiedTimestamp < 0) {
            return false;
        }
        long ifUnmodifiedSince = parseDateHeader(IF_UNMODIFIED_SINCE);
        if (ifUnmodifiedSince == -1) {
            return false;
        }
        // We will perform this validation...
        isNotModified = ifUnmodifiedSince >= lastModifiedTimestamp;
        return true;
    }

    private long parseDateHeader(String headerName) {
        long dateValue = -1;
        try {
            dateValue = mRequest.getDateHeader(headerName);
        } catch (IllegalStateException ex) {
            String headerValue = mRequest.getHeader(headerName);
            if (TextUtils.isEmpty(headerValue)) {
                return -1;
            }
            // Possibly an IE 10 style value: "Wed, 09 Apr 2014 09:57:42 GMT; length=13774"
            int separatorIndex = headerValue.indexOf(';');
            if (separatorIndex != -1) {
                String datePart = headerValue.substring(0, separatorIndex);
                dateValue = parseDateValue(datePart);
            }
        }
        return dateValue;
    }

    private long parseDateValue(String headerValue) {
        if (headerValue == null) {
            return -1;
        }

        if (headerValue.length() >= 3) {
            // Short "0" or "-1" like values are never valid HTTP date headers...
            // Let's only bother with SimpleDateFormat parsing for long enough values.
            return parseDate(headerValue);
        }
        return -1;
    }
}