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
package com.yanzhenjie.andserver.http.session;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Enumeration;

/**
 * Created by Zhenjie Yan on 2018/7/13.
 */
public interface Session {

    /**
     * Returns a string containing the unique identifier assigned to this session.
     */
    @NonNull
    String getId();

    /**
     * Returns the time when this session was created, measured in milliseconds since midnight January 1, 1970 GMT.
     */
    long getCreatedTime();

    /**
     * Returns the last time the client sent a request associated with this session, as the number of milliseconds since
     * midnight January 1, 1970 GMT, and marked by the time the container received the request.
     *
     * <p>Actions that your application takes, such as getting or setting a value associated with the session, do not
     * affect the access time.
     */
    long getLastAccessedTime();

    /**
     * Specifies the time, in seconds, between client requests before the server will invalidate this session.
     *
     * <p>An interval value of zero or less indicates that the session should never timeout.
     *
     * @param interval an integer specifying the number of seconds.
     */
    void setMaxInactiveInterval(int interval);

    /**
     * Returns the maximum time interval, in seconds, that the server will keep this session open between client
     * accesses. After this interval, the server will invalidate the session. The maximum time interval can be set with
     * the {@link #setMaxInactiveInterval(int)} method.
     *
     * <p>A return value of zero or less indicates that the session will never timeout.
     */
    int getMaxInactiveInterval();

    /**
     * Returns the object bound with the specified name in this session, or {@code null} if no object is bound under the
     * name.
     *
     * @param name a string specifying the name of the object.
     */
    @Nullable
    Object getAttribute(@Nullable String name);

    /**
     * Returns an {@code Enumeration} of {@code String} objects containing the names of all the objects bound to this
     * session.
     */
    @NonNull
    Enumeration<String> getAttributeNames();

    /**
     * Binds an object to this session, using the name specified. If an object of the same name is already bound to the
     * session, the object is replaced.
     *
     * <p>If the value passed in is null, this has the same effect as calling {@link #removeAttribute(String)}.
     *
     * @param name the name to which the object is bound, cannot be null.
     * @param value the object to be bound.
     */
    void setAttribute(@NonNull String name, @Nullable Object value);

    /**
     * Removes the object bound with the specified name from this session. If the session does not have an object bound
     * with the specified name, this method does nothing.
     *
     * @param name the name of the object to remove from this session.
     */
    void removeAttribute(@Nullable String name);

    /**
     * Invalidates this session then unbinds any objects bound to it.
     */
    void invalidate();

    /**
     * Returns {@code true} if the client does not yet know about the session or if the client chooses not to join the
     * session. E.g. if the server used only cookie-based sessions, and the client had disabled the use of cookies, then
     * a session would be new on each request.
     *
     * @return {@code true} if the server has created a session, but the client has not yet joined.
     */
    boolean isNew();

    /**
     * @return returns {@code true} is the session is valid, or false if the session is invalid.
     */
    boolean isValid();
}