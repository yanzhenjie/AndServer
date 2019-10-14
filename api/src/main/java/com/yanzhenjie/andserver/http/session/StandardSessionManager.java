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

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.IOException;

/**
 * Created by Zhenjie Yan on 2018/7/26.
 */
public class StandardSessionManager implements SessionManager {

    private IdGenerator mIdGenerator;
    private Store mStore;

    public StandardSessionManager(Context context) {
        this.mIdGenerator = new StandardIdGenerator();

        File sessionDir = new File(context.getCacheDir(), "_andserver_session_");
        this.mStore = new StandardStore(sessionDir);
    }

    @Override
    public void add(@NonNull Session session) throws IOException {
        if (session instanceof StandardSession && session.isNew()) {
            StandardSession standardSession = (StandardSession) session;
            standardSession.setNew(false);
            mStore.replace(standardSession);
        }
    }

    @Override
    public void changeSessionId(@NonNull Session session) {
        if (session instanceof StandardSession) {
            StandardSession standardSession = (StandardSession) session;
            standardSession.setId(mIdGenerator.generateId());
        }
    }

    @NonNull
    @Override
    public Session createSession() {
        StandardSession session = newSession();
        session.setId(mIdGenerator.generateId());
        return session;
    }

    @Nullable
    @Override
    public Session findSession(@NonNull String id) throws IOException, ClassNotFoundException {
        StandardSession session = mStore.getSession(id);
        if (session != null) {
            session.setLastAccessedTime(System.currentTimeMillis());
        }
        return session;
    }

    @Override
    public void remove(@NonNull Session session) {
        if (session instanceof StandardSession) {
            mStore.remove((StandardSession) session);
        }
    }

    private StandardSession newSession() {
        StandardSession session = new StandardSession();
        long currentTime = System.currentTimeMillis();
        session.setCreatedTime(currentTime);
        session.setLastAccessedTime(currentTime);
        session.setNew(true);
        session.setValid(true);
        return session;
    }
}