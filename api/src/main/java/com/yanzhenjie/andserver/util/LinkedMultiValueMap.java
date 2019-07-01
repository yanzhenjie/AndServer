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

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Zhenjie Yan on 2018/6/21.
 */
public class LinkedMultiValueMap<K, V> implements MultiValueMap<K, V>, Cloneable {

    private final Map<K, List<V>> mSource;

    public LinkedMultiValueMap() {
        mSource = new LinkedHashMap<>();
    }

    public LinkedMultiValueMap(int initialCapacity) {
        mSource = new LinkedHashMap<>(initialCapacity);
    }

    public LinkedMultiValueMap(Map<K, List<V>> otherMap) {
        mSource = new LinkedHashMap<>(otherMap);
    }

    @Override
    public void add(K key, V value) {
        List<V> values = mSource.get(key);
        if (values == null) {
            values = new LinkedList<>();
            mSource.put(key, values);
        }
        values.add(value);
    }

    @Override
    public V getFirst(K key) {
        List<V> values = mSource.get(key);
        return (values != null ? values.get(0) : null);
    }

    @Override
    public void set(K key, V value) {
        List<V> values = new LinkedList<>();
        values.add(value);
        this.mSource.put(key, values);
    }

    @Override
    public void setAll(Map<K, V> values) {
        for (Entry<K, V> entry : values.entrySet()) {
            set(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public Map<K, V> toSingleValueMap() {
        LinkedHashMap<K, V> singleValueMap = new LinkedHashMap<>(mSource.size());
        for (Entry<K, List<V>> entry : mSource.entrySet()) {
            singleValueMap.put(entry.getKey(), entry.getValue().get(0));
        }
        return singleValueMap;
    }

    @Override
    public int size() {
        return mSource.size();
    }

    @Override
    public boolean isEmpty() {
        return mSource.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return mSource.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return mSource.containsValue(value);
    }

    @Override
    public List<V> get(Object key) {
        return mSource.get(key);
    }

    @Override
    public List<V> put(K key, List<V> value) {
        return mSource.put(key, value);
    }

    @Override
    public List<V> remove(Object key) {
        return mSource.remove(key);
    }

    @Override
    public void putAll(Map<? extends K, ? extends List<V>> map) {
        mSource.putAll(map);
    }

    @Override
    public void clear() {
        mSource.clear();
    }

    @Override
    public Set<K> keySet() {
        return mSource.keySet();
    }

    @Override
    public Collection<List<V>> values() {
        return mSource.values();
    }

    @Override
    public Set<Entry<K, List<V>>> entrySet() {
        return mSource.entrySet();
    }

    @Override
    public LinkedMultiValueMap<K, V> clone() {
        return new LinkedMultiValueMap<>(this);
    }

    @Override
    public boolean equals(Object obj) {
        return mSource.equals(obj);
    }

    @Override
    public int hashCode() {
        return mSource.hashCode();
    }

    @Override
    public String toString() {
        return mSource.toString();
    }

    public LinkedMultiValueMap<K, V> deepCopy() {
        LinkedMultiValueMap<K, V> copy = new LinkedMultiValueMap<>(mSource.size());
        for (Map.Entry<K, List<V>> entry : mSource.entrySet()) {
            copy.put(entry.getKey(), new LinkedList<>(entry.getValue()));
        }
        return copy;
    }
}