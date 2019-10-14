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
package com.yanzhenjie.andserver.util;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Created by Zhenjie Yan on 2018/6/29.
 */
public class LinkedCaseInsensitiveMap<V> implements Map<String, V>, Serializable, Cloneable {

    private final LinkedHashMap<String, V> mSource;
    private final HashMap<String, String> mCaseInsensitiveKeys;
    private final Locale mLocale;

    /**
     * Create a new instance that stores case-insensitive keys according to the default mLocale, by default in lower
     * case.
     */
    public LinkedCaseInsensitiveMap() {
        this((Locale) null);
    }

    /**
     * Create a new instance that stores case-insensitive keys according to the given mLocale, by default in lower
     * case.
     *
     * @param locale the {@link Locale} to use for case-insensitive key conversion.
     */
    public LinkedCaseInsensitiveMap(Locale locale) {
        this(16, locale);
    }

    /**
     * Create a new instance that wraps a {@link LinkedHashMap} with the given initial capacity and stores
     * case-insensitive keys according to the default mLocale, by default in lower case.
     *
     * @param initialCapacity the initial capacity.
     */
    public LinkedCaseInsensitiveMap(int initialCapacity) {
        this(initialCapacity, null);
    }

    /**
     * Create a new instance that wraps a {@link LinkedHashMap} with the given initial capacity and stores
     * case-insensitive keys according to the given mLocale, by default in lower case.
     *
     * @param initialCapacity the initial capacity.
     * @param locale the mLocale to use for case-insensitive key conversion.
     */
    public LinkedCaseInsensitiveMap(int initialCapacity, Locale locale) {
        this.mSource = new LinkedHashMap<String, V>(initialCapacity) {
            @Override
            public boolean containsKey(Object key) {
                return LinkedCaseInsensitiveMap.this.containsKey(key);
            }

            @Override
            protected boolean removeEldestEntry(Map.Entry<String, V> eldest) {
                boolean isRemoved = LinkedCaseInsensitiveMap.this.removeEldestEntry(eldest);
                if (isRemoved) {
                    mCaseInsensitiveKeys.remove(convertKey(eldest.getKey()));
                }
                return isRemoved;
            }
        };
        this.mCaseInsensitiveKeys = new HashMap<>(initialCapacity);
        this.mLocale = (locale != null ? locale : Locale.getDefault());
    }

    @SuppressWarnings("unchecked")
    private LinkedCaseInsensitiveMap(LinkedCaseInsensitiveMap<V> other) {
        this.mSource = (LinkedHashMap<String, V>) other.mSource.clone();
        this.mCaseInsensitiveKeys = (HashMap<String, String>) other.mCaseInsensitiveKeys.clone();
        this.mLocale = other.mLocale; // No need to clone.
    }


    @Override
    public int size() {
        return this.mSource.size();
    }

    @Override
    public boolean isEmpty() {
        return this.mSource.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return (key instanceof String && this.mCaseInsensitiveKeys.containsKey(convertKey((String) key)));
    }

    @Override
    public boolean containsValue(Object value) {
        return this.mSource.containsValue(value);
    }

    @Override
    public V get(Object key) {
        if (key instanceof String) {
            String caseInsensitiveKey = this.mCaseInsensitiveKeys.get(convertKey((String) key));
            if (caseInsensitiveKey != null) {
                return this.mSource.get(caseInsensitiveKey);
            }
        }
        return null;
    }

    @Override
    public V getOrDefault(Object key, V defaultValue) {
        if (key instanceof String) {
            String caseInsensitiveKey = this.mCaseInsensitiveKeys.get(convertKey((String) key));
            if (caseInsensitiveKey != null) {
                return this.mSource.get(caseInsensitiveKey);
            }
        }
        return defaultValue;
    }

    @Override
    public V put(String key, V value) {
        String oldKey = this.mCaseInsensitiveKeys.put(convertKey(key), key);
        if (oldKey != null && !oldKey.equals(key)) {
            this.mSource.remove(oldKey);
        }
        return this.mSource.put(key, value);
    }

    @Override
    public void putAll(@NonNull Map<? extends String, ? extends V> map) {
        if (map.isEmpty()) {
            return;
        }

        for (Map.Entry<? extends String, ? extends V> entry : map.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public V remove(Object key) {
        if (key instanceof String) {
            String caseInsensitiveKey = this.mCaseInsensitiveKeys.remove(convertKey((String) key));
            if (caseInsensitiveKey != null) {
                return this.mSource.remove(caseInsensitiveKey);
            }
        }
        return null;
    }

    @Override
    public void clear() {
        this.mCaseInsensitiveKeys.clear();
        this.mSource.clear();
    }

    @NonNull
    @Override
    public Set<String> keySet() {
        return this.mSource.keySet();
    }

    @NonNull
    @Override
    public Collection<V> values() {
        return this.mSource.values();
    }

    @NonNull
    @Override
    public Set<Entry<String, V>> entrySet() {
        return this.mSource.entrySet();
    }

    @Override
    public LinkedCaseInsensitiveMap<V> clone() {
        return new LinkedCaseInsensitiveMap<>(this);
    }

    @Override
    public boolean equals(Object obj) {
        return this.mSource.equals(obj);
    }

    @Override
    public int hashCode() {
        return this.mSource.hashCode();
    }

    @Override
    public String toString() {
        return this.mSource.toString();
    }


    /**
     * Return the locale used by this {@code LinkedCaseInsensitiveMap}. Used for case-insensitive key conversion.
     *
     * @see #LinkedCaseInsensitiveMap(Locale)
     * @see #convertKey(String)
     */
    public Locale getLocale() {
        return this.mLocale;
    }

    /**
     * Convert the given key to a case-insensitive key.
     *
     * <p>The default implementation converts the key to lower-case according to this map's locale.
     *
     * @param key the user-specified key.
     *
     * @return the key to use for string.
     *
     * @see String#toLowerCase(Locale)
     */
    protected String convertKey(String key) {
        return key.toLowerCase(getLocale());
    }

    /**
     * Determine whether this map should remove the given eldest entry.
     *
     * @param eldest the candidate entry.
     *
     * @return true for removing it,  false for keeping it.
     *
     * @see LinkedHashMap#removeEldestEntry(Entry)
     */
    protected boolean removeEldestEntry(Map.Entry<String, V> eldest) {
        return false;
    }
}