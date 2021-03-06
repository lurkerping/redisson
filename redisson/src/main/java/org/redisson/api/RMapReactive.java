/**
 * Copyright 2016 Nikita Koksharov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.redisson.api;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.reactivestreams.Publisher;
import org.redisson.api.map.MapLoader;
import org.redisson.api.map.MapWriter;

/**
 *  map functions
 *
 * @author Nikita Koksharov
 *
 * @param <K> key
 * @param <V> value
 */
public interface RMapReactive<K, V> extends RExpirableReactive {

    /**
     * Loads all map entries to this Redis map.
     * 
     * @param replaceExistingValues - <code>true</code> if existed values should be replaced, <code>false</code> otherwise.  
     * @param parallelism - parallelism level, used to increase speed of process execution
     * @return void
     */
    Publisher<Void> loadAll(boolean replaceExistingValues, int parallelism);
    
    /**
     * Loads map entries whose keys are listed in defined <code>keys</code> parameter.
     * 
     * @param keys - map keys
     * @param replaceExistingValues - <code>true</code> if existed values should be replaced, <code>false</code> otherwise.
     * @param parallelism - parallelism level, used to increase speed of process execution
     * @return void
     */
    Publisher<Void> loadAll(Set<? extends K> keys, boolean replaceExistingValues, int parallelism);

    /**
     * Returns size of value mapped by key in bytes
     * 
     * @param key - map key
     * @return size of value
     */
    Publisher<Integer> valueSize(K key);

    /**
     * Gets a map slice contained the mappings with defined <code>keys</code>
     * by one operation.
     * <p>
     * If map doesn't contain value/values for specified key/keys and {@link MapLoader} is defined 
     * then value/values will be loaded in read-through mode. 
     * <p>
     * The returned map is <b>NOT</b> backed by the original map.
     *
     * @param keys - map keys
     * @return Map slice
     */
    Publisher<Map<K, V>> getAll(Set<K> keys);

    /**
     * Associates the specified <code>value</code> with the specified <code>key</code>
     * in batch.
     * <p>
     * If {@link MapWriter} is defined then new map entries are stored in write-through mode. 
     *
     * @param map mappings to be stored in this map
     * @return void
     */
    Publisher<Void> putAll(Map<? extends K, ? extends V> map);

    Publisher<V> addAndGet(K key, Number value);

    Publisher<Boolean> containsValue(Object value);

    Publisher<Boolean> containsKey(Object key);

    Publisher<Integer> size();

    /**
     * Removes <code>keys</code> from map by one operation in async manner.
     * <p>
     * Works faster than <code>{@link #remove(Object, Object)}</code> but doesn't return
     * the value associated with <code>key</code>.
     * <p>
     * If {@link MapWriter} is defined then <code>keys</code>are deleted in write-through mode.
     *
     * @param keys - map keys
     * @return the number of keys that were removed from the hash, not including specified but non existing keys
     */
    Publisher<Long> fastRemove(K ... keys);

    /**
     * Associates the specified <code>value</code> with the specified <code>key</code>
     * in async manner.
     * <p>
     * Works faster than <code>{@link #put(Object, Object)}</code> but not returning
     * the previous value associated with <code>key</code>
     * <p>
     * If {@link MapWriter} is defined then new map entry is stored in write-through mode.
     *
     * @param key - map key
     * @param value - map value
     * @return <code>true</code> if key is a new one in the hash and value was set.
     *         <code>false</code> if key already exists in the hash and the value was updated.
     */
    Publisher<Boolean> fastPut(K key, V value);

    /**
     * Associates the specified <code>value</code> with the specified <code>key</code>
     * only if there is no any association with specified<code>key</code>.
     * <p>
     * Works faster than <code>{@link #putIfAbsent(Object, Object)}</code> but not returning
     * the previous value associated with <code>key</code>
     * <p>
     * If {@link MapWriter} is defined then new map entry is stored in write-through mode.
     *
     * @param key - map key
     * @param value - map value
     * @return <code>true</code> if key is a new one in the hash and value was set.
     *         <code>false</code> if key already exists in the hash and change hasn't been made.
     */
    Publisher<Boolean> fastPutIfAbsent(K key, V value);
    
    /**
     * Read all keys at once
     *
     * @return keys
     */
    Publisher<Set<K>> readAllKeySet();

    /**
     * Read all values at once
     *
     * @return values
     */
    Publisher<Collection<V>> readAllValues();

    /**
     * Read all map entries at once
     *
     * @return entries
     */
    Publisher<Set<Entry<K, V>>> readAllEntrySet();

    /**
     * Read all map as local instance at once
     *
     * @return map
     */
    Publisher<Map<K, V>> readAllMap();

    /**
     * Returns the value to which the specified key is mapped,
     * or {@code null} if this map contains no mapping for the key.
     * <p>
     * If map doesn't contain value for specified key and {@link MapLoader} is defined 
     * then value will be loaded in read-through mode. 
     *
     * @param key the key whose associated value is to be returned
     * @return the value to which the specified key is mapped, or
     *         {@code null} if this map contains no mapping for the key
     */
    Publisher<V> get(K key);

    /**
     * Associates the specified <code>value</code> with the specified <code>key</code>
     * in async manner.
     * <p>
     * If {@link MapWriter} is defined then new map entry is stored in write-through mode.
     *
     * @param key - map key
     * @param value - map value
     * @return previous associated value
     */
    Publisher<V> put(K key, V value);

    /**
     * Removes <code>key</code> from map and returns associated value in async manner.
     * <p>
     * If {@link MapWriter} is defined then <code>key</code>is deleted in write-through mode.
     *
     * @param key - map key
     * @return deleted value or <code>null</code> if there wasn't any association
     */
    Publisher<V> remove(K key);

    /**
     * Replaces previous value with a new <code>value</code> associated with the <code>key</code>.
     * If there wasn't any association before then method returns <code>null</code>.
     * <p>
     * If {@link MapWriter} is defined then new <code>value</code>is written in write-through mode.
     *
     * @param key - map key
     * @param value - map value
     * @return previous associated value 
     *         or <code>null</code> if there wasn't any association and change hasn't been made
     */
    Publisher<V> replace(K key, V value);

    /**
     * Replaces previous <code>oldValue</code> with a <code>newValue</code> associated with the <code>key</code>.
     * If previous value doesn't exist or equal to <code>oldValue</code> then method returns <code>false</code>.
     * <p>
     * If {@link MapWriter} is defined then <code>newValue</code>is written in write-through mode.
     *
     * @param key - map key
     * @param oldValue - map old value
     * @param newValue - map new value
     * @return <code>true</code> if value has been replaced otherwise <code>false</code>.
     */
    Publisher<Boolean> replace(K key, V oldValue, V newValue);

    /**
     * Removes <code>key</code> from map only if it associated with <code>value</code>.
     * <p>
     * If {@link MapWriter} is defined then <code>key</code>is deleted in write-through mode.
     *
     * @param key - map key
     * @param value - map value
     * @return <code>true</code> if map entry has been replaced otherwise <code>false</code>.
     */
    Publisher<Boolean> remove(Object key, Object value);

    /**
     * Associates the specified <code>value</code> with the specified <code>key</code>
     * only if there is no any association with specified<code>key</code>.
     * <p>
     * If {@link MapWriter} is defined then new map entry is stored in write-through mode.
     *
     * @param key - map key
     * @param value - map value
     * @return <code>null</code> if key is a new one in the hash and value was set.
     *         Previous value if key already exists in the hash and change hasn't been made.
     */
    Publisher<V> putIfAbsent(K key, V value);

    Publisher<Map.Entry<K, V>> entryIterator();

    Publisher<V> valueIterator();

    Publisher<K> keyIterator();

}
