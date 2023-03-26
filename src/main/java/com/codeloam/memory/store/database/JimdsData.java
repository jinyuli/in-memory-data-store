package com.codeloam.memory.store.database;

import com.codeloam.memory.store.network.ByteWord;

/**
 * Base class for all data structures.
 *
 * @author jinyu.li
 * @since 1.0
 */
public abstract class JimdsData {
    public abstract DataType getDataType();
    public abstract ByteWord get();
}
