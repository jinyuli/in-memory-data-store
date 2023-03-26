package com.codeloam.memory.store.database;

import com.codeloam.memory.store.network.ByteWord;

/**
 * @author jinyu.li
 * @since 1.0
 */
public abstract class JimdsNumber extends JimdsData {
    @Override
    public DataType getDataType() {
        return DataType.Number;
    }

    /**
     * Whether the data is decimal(float or double).
     *
     * @return true if data is decimal
     */
    public abstract boolean isDecimal();

    public abstract JimdsNumber setValue(long value);
    public abstract JimdsNumber setValue(double value);

    public abstract JimdsNumber add(long value);
    public abstract JimdsNumber add(double value);
}
