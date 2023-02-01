package com.codeloam.memory.store.database;

import com.codeloam.memory.store.network.ByteWord;

/**
 * String type interface.
 * As there may be multiple implementations, so use interface here.
 * @author jinyu.li
 * @since 1.0
 */
public abstract class JimdsString extends JimdsData {
    @Override
    public DataType getDataType() {
        return DataType.String;
    }

    /**
     * Current string length.
     *
     * @return string length
     */
    public abstract int size();

    /**
     * Get current string representation.
     *
     * @return a ByteWord
     */
    public abstract ByteWord get();
}
