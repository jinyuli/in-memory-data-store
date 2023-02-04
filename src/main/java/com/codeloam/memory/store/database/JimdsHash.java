package com.codeloam.memory.store.database;

/**
 * Abstract Hash.
 *
 * <p>Null key is not accepted.
 *
 * @author jinyu.li
 * @since 1.0
 */
public abstract class JimdsHash<K extends Comparable<K>, T> extends JimdsData {
    public DataType getDataType() {
        return DataType.Hash;
    }

    /**
     * Element count.
     *
     * @return size
     */
    public abstract int size();

    /**
     * Get value by key.
     *
     * @param key key
     *
     * @return value or null if not exist
     */
    public abstract T get(K key);

    /**
     * Store the given key and value.
     *
     * @param key key, not null
     * @param value value, not null
     *
     * @return old value if exists, or null
     * @throws NullPointerException if key or value is null
     */
    public abstract T set(K key, T value);

    /**
     * Whether given key exists in hash.
     *
     * @param key key
     * @return true if key is in hash, or false
     */
    public abstract boolean exist(K key);
}
