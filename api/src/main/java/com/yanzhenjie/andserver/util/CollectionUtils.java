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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * Created by Zhenjie Yan on 2018/7/11.
 *
 * @deprecated use apache commons-collection instead.
 */
@Deprecated
public abstract class CollectionUtils {

    /**
     * Return {@code true} if the supplied Collection is {@code null} or empty. Otherwise, return {@code false}.
     *
     * @param collection the Collection to check.
     *
     * @return whether the given Collection is empty.
     */
    public static boolean isEmpty(Collection<?> collection) {
        return (collection == null || collection.isEmpty());
    }

    /**
     * Return {@code true} if the supplied Map is {@code null} or empty. Otherwise, return {@code false}.
     *
     * @param map the Map to check.
     *
     * @return whether the given Map is empty.
     */
    public static boolean isEmpty(Map<?, ?> map) {
        return (map == null || map.isEmpty());
    }

    /**
     * Convert the supplied array into a List. StandardCookieProcessor primitive array gets converted into a List of the
     * appropriate wrapper type.
     *
     * <p><b>NOTE:</b> Generally prefer the standard {@link Arrays#asList} method. This {@code arrayToList} method is
     * just meant to deal with an incoming Object value that might be an {@code Object[]} or a primitive array at
     * runtime.
     *
     * <p>StandardCookieProcessor {@code null} source value will be converted to an empty List.
     *
     * @param source the (potentially primitive) array.
     *
     * @return the converted List result.
     */
    public static List arrayToList(Object source) {
        return Arrays.asList(ObjectUtils.toObjectArray(source));
    }

    /**
     * Merge the given array into the given Collection.
     *
     * @param array the array to merge (may be {@code null}).
     * @param collection the target Collection to merge the array into.
     */
    @SuppressWarnings("unchecked")
    public static <E> void mergeArrayIntoCollection(Object array, Collection<E> collection) {
        if (collection == null) {
            throw new IllegalArgumentException("Collection must not be null");
        }
        Object[] arr = ObjectUtils.toObjectArray(array);
        for (Object elem: arr) {
            collection.add((E) elem);
        }
    }

    /**
     * Merge the given Properties instance into the given Map, copying all properties (key-value pairs) over. <p>Uses
     * {@code Properties.propertyNames()} to even catch default properties linked into the original Properties
     * instance.
     *
     * @param props the Properties instance to merge (may be {@code null}).
     * @param map the target Map to merge the properties into.
     */
    @SuppressWarnings("unchecked")
    public static <K, V> void mergePropertiesIntoMap(Properties props, Map<K, V> map) {
        if (map == null) {
            throw new IllegalArgumentException("Map must not be null");
        }
        if (props != null) {
            for (Enumeration<?> en = props.propertyNames(); en.hasMoreElements(); ) {
                String key = (String) en.nextElement();
                Object value = props.get(key);
                if (value == null) {
                    // Allow for defaults fallback or potentially overridden accessor...
                    value = props.getProperty(key);
                }
                map.put((K) key, (V) value);
            }
        }
    }


    /**
     * Check whether the given Iterator contains the given element.
     *
     * @param iterator the Iterator to check.
     * @param element the element to look for.
     *
     * @return {@code true} if found, {@code false} else.
     */
    public static boolean contains(Iterator<?> iterator, Object element) {
        if (iterator != null) {
            while (iterator.hasNext()) {
                Object candidate = iterator.next();
                if (ObjectUtils.nullSafeEquals(candidate, element)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Check whether the given Enumeration contains the given element.
     *
     * @param enumeration the Enumeration to check.
     * @param element the element to look for.
     *
     * @return {@code true} if found, {@code false} else.
     */
    public static boolean contains(Enumeration<?> enumeration, Object element) {
        if (enumeration != null) {
            while (enumeration.hasMoreElements()) {
                Object candidate = enumeration.nextElement();
                if (ObjectUtils.nullSafeEquals(candidate, element)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Check whether the given Collection contains the given element instance. <p>Enforces the given instance to be
     * present, rather than returning {@code true} for an equal element as well.
     *
     * @param collection the Collection to check.
     * @param element the element to look for.
     *
     * @return {@code true} if found, {@code false} else.
     */
    public static boolean containsInstance(Collection<?> collection, Object element) {
        if (collection != null) {
            for (Object candidate: collection) {
                if (candidate == element) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Return {@code true} if any element in '{@code candidates}' is contained in '{@code source}'; otherwise returns
     * {@code false}.
     *
     * @param source the source Collection.
     * @param candidates the candidates to search for.
     *
     * @return whether any of the candidates has been found.
     */
    public static boolean containsAny(Collection<?> source, Collection<?> candidates) {
        if (isEmpty(source) || isEmpty(candidates)) {
            return false;
        }
        for (Object candidate: candidates) {
            if (source.contains(candidate)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Return the first element in '{@code candidates}' that is contained in '{@code source}'. If no element in '{@code
     * candidates}' is present in '{@code source}' returns {@code null}. Iteration order is {@link Collection}
     * implementation specific.
     *
     * @param source the source Collection.
     * @param candidates the candidates to search for.
     *
     * @return the first present object, or {@code null} if not found.
     */
    @SuppressWarnings("unchecked")
    public static <E> E findFirstMatch(Collection<?> source, Collection<E> candidates) {
        if (isEmpty(source) || isEmpty(candidates)) {
            return null;
        }
        for (Object candidate: candidates) {
            if (source.contains(candidate)) {
                return (E) candidate;
            }
        }
        return null;
    }

    /**
     * Find a single value of the given type in the given Collection.
     *
     * @param collection the Collection to search.
     * @param type the type to look for.
     *
     * @return a value of the given type found if there is a clear match, or {@code null} if none or more than one such
     *     value found.
     */
    @SuppressWarnings("unchecked")
    public static <T> T findValueOfType(Collection<?> collection, Class<T> type) {
        if (isEmpty(collection)) {
            return null;
        }
        T value = null;
        for (Object element: collection) {
            if (type == null || type.isInstance(element)) {
                if (value != null) {
                    // More than one value found... no clear single value.
                    return null;
                }
                value = (T) element;
            }
        }
        return value;
    }

    /**
     * Find a single value of one of the given types in the given Collection: searching the Collection for a value of
     * the first type, then searching for a value of the second type, etc.
     *
     * @param collection the collection to search.
     * @param types the types to look for, in prioritized order.
     *
     * @return a value of one of the given types found if there is a clear match, or {@code null} if none or more than
     *     one such value found.
     */
    public static Object findValueOfType(Collection<?> collection, Class<?>[] types) {
        if (isEmpty(collection) || ObjectUtils.isEmpty(types)) {
            return null;
        }
        for (Class<?> type: types) {
            Object value = findValueOfType(collection, type);
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    /**
     * Determine whether the given Collection only contains a single unique object.
     *
     * @param collection the Collection to check.
     *
     * @return {@code true} if the collection contains a single reference or multiple references to the same instance,
     *     {@code false} else.
     */
    public static boolean hasUniqueObject(Collection<?> collection) {
        if (isEmpty(collection)) {
            return false;
        }
        boolean hasCandidate = false;
        Object candidate = null;
        for (Object elem: collection) {
            if (!hasCandidate) {
                hasCandidate = true;
                candidate = elem;
            } else if (candidate != elem) {
                return false;
            }
        }
        return true;
    }

    /**
     * Find the common element type of the given Collection, if any.
     *
     * @param collection the Collection to check.
     *
     * @return the common element type, or {@code null} if no clear common type has been found (or the collection was
     *     empty).
     */
    public static Class<?> findCommonElementType(Collection<?> collection) {
        if (isEmpty(collection)) {
            return null;
        }
        Class<?> candidate = null;
        for (Object val: collection) {
            if (val != null) {
                if (candidate == null) {
                    candidate = val.getClass();
                } else if (candidate != val.getClass()) {
                    return null;
                }
            }
        }
        return candidate;
    }

    /**
     * Marshal the elements from the given enumeration into an array of the given type. Enumeration elements must be
     * assignable to the type of the given array. The array returned will be a different instance than the array given.
     */
    public static <A, E extends A> A[] toArray(Enumeration<E> enumeration, A[] array) {
        ArrayList<A> elements = new ArrayList<>();
        while (enumeration.hasMoreElements()) {
            elements.add(enumeration.nextElement());
        }
        return elements.toArray(array);
    }

    /**
     * Adapt an enumeration to an iterator.
     *
     * @param enumeration the enumeration.
     *
     * @return the iterator.
     */
    public static <E> Iterator<E> toIterator(Enumeration<E> enumeration) {
        return new EnumerationIterator<>(enumeration);
    }

    /**
     * Adapt a {@code Map<K, List<V>>} to an {@code MultiValueMap<K, V>}.
     *
     * @param map the original map.
     *
     * @return the multi-value map.
     */
    public static <K, V> MultiValueMap<K, V> toMultiValueMap(Map<K, List<V>> map) {
        return new MultiValueMapAdapter<>(map);
    }

    /**
     * Return an unmodifiable view of the specified multi-value map.
     *
     * @param map the map for which an unmodifiable view is to be returned.
     *
     * @return an unmodifiable view of the specified multi-value map.
     */
    @SuppressWarnings("unchecked")
    public static <K, V> MultiValueMap<K, V> unmodifiableMultiValueMap(MultiValueMap<? extends K, ? extends V> map) {
        Assert.notNull(map, "'map' must not be null");
        Map<K, List<V>> result = new LinkedHashMap<>(map.size());
        for (Map.Entry<? extends K, ? extends List<? extends V>> entry: map.entrySet()) {
            List<? extends V> values = Collections.unmodifiableList(entry.getValue());
            result.put(entry.getKey(), (List<V>) values);
        }
        Map<K, List<V>> unmodifiableMap = Collections.unmodifiableMap(result);
        return toMultiValueMap(unmodifiableMap);
    }


    /**
     * Iterator wrapping an Enumeration.
     */
    private static class EnumerationIterator<E> implements Iterator<E> {

        private final Enumeration<E> enumeration;

        public EnumerationIterator(Enumeration<E> enumeration) {
            this.enumeration = enumeration;
        }

        @Override
        public boolean hasNext() {
            return this.enumeration.hasMoreElements();
        }

        @Override
        public E next() {
            return this.enumeration.nextElement();
        }

        @Override
        public void remove() throws UnsupportedOperationException {
            throw new UnsupportedOperationException("Not supported");
        }
    }

    /**
     * Adapts a Map to the MultiValueMap contract.
     */
    private static class MultiValueMapAdapter<K, V> implements MultiValueMap<K, V>, Serializable {

        private final Map<K, List<V>> mMap;

        public MultiValueMapAdapter(Map<K, List<V>> map) {
            Assert.notNull(map, "'map' must not be null");
            this.mMap = map;
        }

        @Override
        public void add(K key, V value) {
            List<V> values = this.mMap.get(key);
            if (values == null) {
                values = new LinkedList<>();
                this.mMap.put(key, values);
            }
            values.add(value);
        }

        @Override
        public V getFirst(K key) {
            List<V> values = this.mMap.get(key);
            return (values != null ? values.get(0) : null);
        }

        @Override
        public void set(K key, V value) {
            List<V> values = new LinkedList<>();
            values.add(value);
            this.mMap.put(key, values);
        }

        @Override
        public void setAll(Map<K, V> values) {
            for (Entry<K, V> entry: values.entrySet()) {
                set(entry.getKey(), entry.getValue());
            }
        }

        @Override
        public Map<K, V> toSingleValueMap() {
            LinkedHashMap<K, V> singleValueMap = new LinkedHashMap<>(this.mMap.size());
            for (Entry<K, List<V>> entry: mMap.entrySet()) {
                singleValueMap.put(entry.getKey(), entry.getValue().get(0));
            }
            return singleValueMap;
        }

        @Override
        public int size() {
            return this.mMap.size();
        }

        @Override
        public boolean isEmpty() {
            return this.mMap.isEmpty();
        }

        @Override
        public boolean containsKey(Object key) {
            return this.mMap.containsKey(key);
        }

        @Override
        public boolean containsValue(Object value) {
            return this.mMap.containsValue(value);
        }

        @Override
        public List<V> get(Object key) {
            return this.mMap.get(key);
        }

        @Override
        public List<V> put(K key, List<V> value) {
            return this.mMap.put(key, value);
        }

        @Override
        public List<V> remove(Object key) {
            return this.mMap.remove(key);
        }

        @Override
        public void putAll(Map<? extends K, ? extends List<V>> map) {
            this.mMap.putAll(map);
        }

        @Override
        public void clear() {
            this.mMap.clear();
        }

        @Override
        public Set<K> keySet() {
            return this.mMap.keySet();
        }

        @Override
        public Collection<List<V>> values() {
            return this.mMap.values();
        }

        @Override
        public Set<Entry<K, List<V>>> entrySet() {
            return this.mMap.entrySet();
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            return mMap.equals(other);
        }

        @Override
        public int hashCode() {
            return this.mMap.hashCode();
        }

        @Override
        public String toString() {
            return this.mMap.toString();
        }
    }
}