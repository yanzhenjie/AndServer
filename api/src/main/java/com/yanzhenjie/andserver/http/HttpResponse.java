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
package com.yanzhenjie.andserver.http;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yanzhenjie.andserver.http.cookie.Cookie;

import java.util.Enumeration;
import java.util.List;

/**
 * Created by Zhenjie Yan on 2018/6/12.
 */
public interface HttpResponse extends StatusCode, HttpHeaders {

    /**
     * Sets the status code for this response.
     *
     * <p>This method preserves any cookies and other response headers.
     *
     * <p>Valid status codes are those in the 2XX, 3XX, 4XX, and 5XX ranges. Other status codes will be considered to be
     * specific.
     */
    void setStatus(int sc);

    /**
     * Gets the current status code of this response.
     *
     * @return status code.
     */
    int getStatus();

    /**
     * Sets a response header with the given name and value. If the header had already been set, the new value overwrites
     * the previous one. The {@link #containsHeader(String)} method can be used to test for the presence of a header before
     * setting its value.
     *
     * @see #containsHeader(String)
     * @see #addHeader(String, String)
     */
    void setHeader(@NonNull String name, @NonNull String value);

    /**
     * Adds a response header with the given name and value. This method allows response headers to have multiple values.
     *
     * @see #setHeader(String, String)
     */
    void addHeader(@NonNull String name, @NonNull String value);

    /**
     * Gets the value of the response header with the given name.
     *
     * <p>If a response header with the given name exists and contains multiple values, the value that was added first
     * will be returned.
     *
     * @param name the name of the response header.
     *
     * @return the value of the response header with the given name, or {@code null} if no header with the given name has
     *     been set on this response.
     */
    @Nullable
    String getHeader(@NonNull String name);

    /**
     * Sets a response header with the given name and date-value. The date is specified in terms of milliseconds since the
     * epoch.
     *
     * <p>If the header had already been set, the new value overwrites the previous one. The {@link
     * #containsHeader(String)} method can be used to test for the presence of a header before setting its value.
     *
     * @see #containsHeader(String)
     * @see #addDateHeader(String, long)
     */
    void setDateHeader(@NonNull String name, long date);

    /**
     * Adds a response header with the given name and date-value. The date is specified in terms of milliseconds since the
     * epoch. This method allows response headers to have multiple values.
     *
     * @see #setDateHeader(String, long)
     */
    void addDateHeader(@NonNull String name, long date);

    /**
     * Sets a response header with the given name and integer value. If the header had already been set, the new value
     * overwrites the previous one. The {@link #containsHeader(String)} method can be used to test for the presence of a
     * header before setting its value.
     *
     * @see #containsHeader(String)
     * @see #addIntHeader(String, int)
     */
    void setIntHeader(@NonNull String name, int value);

    /**
     * Adds a response header with the given name and integer value. This method allows response headers to have multiple
     * values.
     *
     * @see #setIntHeader(String, int)
     */
    void addIntHeader(@NonNull String name, int value);

    /**
     * Returns a boolean indicating whether the named response header has already been set.
     */
    boolean containsHeader(@NonNull String name);

    /**
     * Gets the values of the response header with the given name.
     *
     * @param name the name of the response header.
     *
     * @return a {@link List} of the values of the response header with the given name.
     */
    @NonNull
    List<String> getHeaders(@NonNull String name);

    /**
     * Gets the names of the headers of this response.
     *
     * @return a {@link Enumeration} of the names of the headers of this response.
     */
    @NonNull
    List<String> getHeaderNames();

    /**
     * Adds the specified cookie to the response. This method can be called multiple times to set more than one cookie.
     *
     * @param cookie the cookie to return to the client.
     */
    void addCookie(@NonNull Cookie cookie);

    /**
     * Sends a temporary redirect response to the client using the specified redirect location URL and clears the buffer.
     * The buffer will be replaced with the data set by this method. Calling this method sets the status code to {@link
     * StatusCode#SC_FOUND}.
     */
    void sendRedirect(@NonNull String location);

    /**
     * Set the response body.
     *
     * @param body write the message content sent to the client.
     */
    void setBody(ResponseBody body);
}