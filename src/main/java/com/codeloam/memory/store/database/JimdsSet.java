package com.codeloam.memory.store.database;

/**
 * Set.
 *
 * @author jinyu.li
 * @since 1.0
 */
public abstract class JimdsSet<K extends Comparable<K>> extends JimdsData {
    @Override
    public DataType getDataType() {
        return DataType.Set;
    }

    /**
     * Element count.
     *
     * @return size
     */
    public abstract int size();

    /**
     * Add an object to set.
     *
     * @param key key
     */
    public abstract void add(K key);

    /**
     * Whether given key exists in set.
     *
     * @param key key
     * @return true if key is in hash, or false
     */
    public abstract boolean exist(K key);
}
