package com.codeloam.memory.store.database;

/**
 * Sorted set.
 *
 * @author jinyu.li
 * @since 1.0
 */
public abstract class JimdsSortedSet extends JimdsData {
    @Override
    public DataType getDataType() {
        return DataType.SortedSet;
    }
}
