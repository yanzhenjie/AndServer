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

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yanzhenjie.andserver.util.Assert;
import com.yanzhenjie.andserver.util.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by Zhenjie Yan on 2018/7/26.
 */
public class StandardStore implements Store {

    private File mDirectory;

    public StandardStore(File directory) {
        this.mDirectory = directory;
    }

    @Override
    public boolean replace(@NonNull StandardSession session) throws IOException {
        Assert.notNull(session, "The session can not be null.");

        String id = session.getId();
        if (TextUtils.isEmpty(id)) {
            throw new IllegalStateException("The session id can not be empty or null.");
        }

        ObjectOutputStream writer = null;
        try {
            if (!IOUtils.createFolder(mDirectory)) {
                return false;
            }

            File file = new File(mDirectory, id);
            if (!IOUtils.createNewFile(file)) {
                return false;
            }

            writer = new ObjectOutputStream(new FileOutputStream(file));
            session.writeObject(writer);
            return true;
        } catch (IOException e) {
            IOUtils.delFileOrFolder(new File(mDirectory, id));
            throw e;
        } finally {
            IOUtils.closeQuietly(writer);
        }
    }

    @Nullable
    @Override
    public StandardSession getSession(@NonNull String id) throws IOException, ClassNotFoundException {
        if (TextUtils.isEmpty(id)) {
            throw new IllegalArgumentException("The id can not be empty or null.");
        }

        ObjectInputStream reader = null;
        try {
            File file = new File(mDirectory, id);
            if (!file.exists() || file.isDirectory()) {
                return null;
            }

            reader = new ObjectInputStream(new FileInputStream(file));
            StandardSession session = new StandardSession();
            session.readObject(reader);
            return session;
        } catch (IOException e) {
            IOUtils.delFileOrFolder(new File(mDirectory, id));
            throw e;
        } finally {
            IOUtils.closeQuietly(reader);
        }
    }

    @Override
    public boolean remove(@NonNull StandardSession session) {
        String id = session.getId();
        if (TextUtils.isEmpty(id)) {
            throw new IllegalStateException("The session id can not be empty or null.");
        }
        return IOUtils.delFileOrFolder(new File(mDirectory, session.getId()));
    }
}