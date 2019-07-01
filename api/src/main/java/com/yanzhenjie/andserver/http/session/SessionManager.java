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

import java.io.IOException;

/**
 * Created by Zhenjie Yan on 2018/7/26.
 */
public interface SessionManager {

    /**
     * Add this session to the set of active sessions for this {@code SessionManager}.
     *
     * @param session session to be added.
     *
     * @throws IOException if an output error occurs while processing this request.
     */
    void add(@NonNull Session session) throws IOException;

    /**
     * Change the session ID of the current session to a new randomly generated session ID.
     *
     * @param session the session to change the session ID for.
     */
    void changeSessionId(@NonNull Session session);

    /**
     * Create a new session object.
     *
     * @return an empty session object.
     */
    @NonNull
    Session createSession();

    /**
     * Return the active session, with the specified session id (if any); otherwise return {@code null}.
     *
     * @param id the session id for the session to be returned.
     *
     * @return the request session or {@code null}.
     *
     * @throws IllegalStateException if a new session cannot be instantiated for any reason.
     * @throws IOException if an output error occurs while processing this request.
     */
    @Nullable
    Session findSession(@NonNull String id) throws IOException, ClassNotFoundException;

    /**
     * Remove this session from the active sessions for this {@code SessionManager}.
     *
     * @param session session to be removed.
     */
    void remove(@NonNull Session session);
}