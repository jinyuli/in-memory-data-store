package com.codeloam.memory.store.database.simple;

import com.codeloam.memory.store.database.JimdsHash;
import com.codeloam.memory.store.network.ByteWord;

import java.util.HashMap;

/**
 * Simple implementation of Hash, use Java builtin HashMap.
 *
 * @author jinyu.li
 * @since 1.0
 */
public class SimpleHash<K extends Comparable<K>, V> extends JimdsHash<K, V> {
    private final HashMap<K, V> hashMap;

    public SimpleHash() {
        hashMap = new HashMap<>();
    }

    @Override
    public int size() {
        return hashMap.size();
    }

    @Override
    public V get(K key) {
        return hashMap.get(key);
    }

    @Override
    public V set(K key, V value) {
        return hashMap.put(key, value);
    }

    @Override
    public boolean exist(K key) {
        return hashMap.containsKey(key);
    }

    @Override
    public ByteWord getData() {
        return null;
    }
}
