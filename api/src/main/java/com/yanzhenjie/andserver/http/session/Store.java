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
interface Store {

    /**
     * Increase the session to the persistent store.
     *
     * @param session the session.
     *
     * @return true if it is successfully added or replaced, otherwise is false.
     *
     * @throws IOException if an output error occurs while processing this request.
     */
    boolean replace(@NonNull StandardSession session) throws IOException;

    /**
     * Get the session from the persistent store.
     *
     * @param id the session ID.
     *
     * @return a session object.
     *
     * @throws IOException if the input error occurs while processing this request.
     */
    @Nullable
    StandardSession getSession(@NonNull String id) throws IOException, ClassNotFoundException;

    /**
     * Remove the session from the persistent store.
     *
     * @param session the session.
     *
     * @return true if successful removal, otherwise is false.
     */
    boolean remove(@NonNull StandardSession session);
}