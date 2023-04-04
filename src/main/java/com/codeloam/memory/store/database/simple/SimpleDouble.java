package com.codeloam.memory.store.database.simple;

import com.codeloam.memory.store.database.JimdsNumber;
import com.codeloam.memory.store.network.ByteWord;

/**
 * Double value.
 *
 * @author jinyu.li
 * @since 1.0
 */
public class SimpleDouble extends JimdsNumber {
    private double value;

    public SimpleDouble(double value) {
        this.value = value;
    }

    @Override
    public boolean isDecimal() {
        return true;
    }

    @Override
    public ByteWord getData() {
        return ByteWord.create(value);
    }

    public double getValue() {
        return value;
    }

    @Override
    public JimdsNumber add(long value) {
        this.value += value;
        return this;
    }

    @Override
    public JimdsNumber add(double value) {
        this.value += value;
        return this;
    }

    @Override
    public JimdsNumber setValue(double value) {
        this.value = value;
        return this;
    }

    @Override
    public JimdsNumber setValue(long value) {
        this.value = value;
        return this;
    }
}
