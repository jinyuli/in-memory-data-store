package com.codeloam.memory.store.database.simple;

import com.codeloam.memory.store.database.JimdsNumber;
import com.codeloam.memory.store.network.ByteWord;

/**
 * Long value.
 *
 * @author jinyu.li
 * @since 1.0
 */
public class SimpleLong extends JimdsNumber {
    private long value;

    public SimpleLong(long value) {
        this.value = value;
    }

    @Override
    public boolean isDecimal() {
        return false;
    }

    @Override
    public ByteWord getData() {
        return ByteWord.create(value);
    }

    public long getValue() {
        return value;
    }

    @Override
    public JimdsNumber setValue(long value) {
        this.value = value;
        return this;
    }

    @Override
    public JimdsNumber setValue(double value) {
        return new SimpleDouble(value);
    }

    @Override
    public JimdsNumber add(long value) {
        this.value += value;
        return this;
    }

    @Override
    public JimdsNumber add(double value) {
        return new SimpleDouble(this.value + value);
    }
}
