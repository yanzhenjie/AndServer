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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Zhenjie Yan on 2018/7/26.
 */
public class StandardSession implements Session {

    private static final String EMPTY_ARRAY[] = new String[0];

    private String id;
    private long createdTime;
    private long lastAccessedTime;
    private int maxInactiveInterval = -1;
    private Map<String, Object> mAttributes = new ConcurrentHashMap<>();
    private boolean isNew;
    private boolean isValid;

    public StandardSession() {
    }

    public void setId(@NonNull String id) {
        if (TextUtils.isEmpty(id)) {
            throw new IllegalArgumentException("The id can not be empty or null.");
        }
        this.id = id;
    }

    @NonNull
    @Override
    public String getId() {
        return id;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    @Override
    public long getCreatedTime() {
        return createdTime;
    }

    public void setLastAccessedTime(long lastAccessedTime) {
        this.lastAccessedTime = lastAccessedTime;
    }

    @Override
    public long getLastAccessedTime() {
        validate();

        return lastAccessedTime;
    }

    @Override
    public void setMaxInactiveInterval(int interval) {
        this.maxInactiveInterval = interval;
    }

    @Override
    public int getMaxInactiveInterval() {
        return maxInactiveInterval;
    }

    @Override
    public Object getAttribute(@Nullable String name) {
        validate();

        if (name == null) {
            return null;
        }
        return mAttributes.get(name);
    }

    @NonNull
    @Override
    public Enumeration<String> getAttributeNames() {
        validate();

        return Collections.enumeration(new HashSet<>(mAttributes.keySet()));
    }

    @Override
    public void setAttribute(@NonNull String name, @Nullable Object value) {
        validate();

        Assert.notNull(name, "The name cannot be null.");

        if (value == null) {
            return;
        }
        mAttributes.put(name, value);
    }

    @Override
    public void removeAttribute(@Nullable String name) {
        validate();

        if (name == null) {
            return;
        }
        mAttributes.remove(name);
    }

    @Override
    public void invalidate() {
        validate();

        this.isValid = false;
    }

    public void setNew(boolean aNew) {
        this.isNew = aNew;
    }

    @Override
    public boolean isNew() {
        validate();

        return isNew;
    }

    private void validate() {
        if (!isValid()) {
            throw new IllegalStateException("This session is invalid.");
        }
    }

    public void setValid(boolean valid) {
        this.isValid = valid;
    }

    @Override
    public boolean isValid() {
        if (!isValid) {
            return false;
        }

        if (maxInactiveInterval > 0) {
            long inactiveInterval = System.currentTimeMillis() - lastAccessedTime;
            int timeIdle = (int) (inactiveInterval / 1000L);
            if (timeIdle >= maxInactiveInterval) {
                isValid = false;
            }
        } else {
            isValid = true;
        }

        return isValid;
    }

    /**
     * Write attribute values to the stream.
     *
     * @param stream stream.
     *
     * @throws IOException if the output error occurs while processing this request.
     */
    public void writeObject(@NonNull ObjectOutputStream stream) throws IOException {
        stream.writeObject(id);
        stream.writeLong(createdTime);
        stream.writeLong(lastAccessedTime);
        stream.writeInt(maxInactiveInterval);
        stream.writeBoolean(isNew);
        stream.writeBoolean(isValid);
        stream.writeInt(mAttributes.size());
        String keys[] = mAttributes.keySet().toArray(EMPTY_ARRAY);
        for (String key: keys) {
            Object value = mAttributes.get(key);
            if (value != null && value instanceof Serializable) {
                stream.writeObject(key);
                stream.writeObject(value);
            }
        }
    }

    /**
     * Read attribute values from the stream.
     *
     * @param stream stream.
     *
     * @throws IllegalStateException if a new session cannot be instantiated for any reason.
     * @throws IOException if the input error occurs while processing this request.
     */
    public void readObject(@NonNull ObjectInputStream stream) throws IOException, ClassNotFoundException {
        id = (String) stream.readObject();
        createdTime = stream.readLong();
        lastAccessedTime = stream.readLong();
        maxInactiveInterval = stream.readInt();
        isNew = stream.readBoolean();
        isValid = stream.readBoolean();

        // Deserialize the attribute count and attribute values
        int size = stream.readInt();
        for (int i = 0; i < size; i++) {
            String name = (String) stream.readObject();
            Object value = stream.readObject();
            mAttributes.put(name, value);
        }
    }
}