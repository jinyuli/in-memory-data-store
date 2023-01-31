package com.codeloam.memory.store.database.simple;

import com.codeloam.memory.store.database.JimdsString;
import com.codeloam.memory.store.network.ByteWord;

/**
 * Simple string, use byte array.
 *
 * @author jinyu.li
 * @since 1.0
 */
public class SimpleString extends JimdsString {
    private final ByteWord byteWord;

    public SimpleString(ByteWord byteWord) {
        if (byteWord == null) {
            throw new NullPointerException("null string");
        }
        this.byteWord = byteWord;
    }

    public SimpleString(byte[] bytes) {
        this.byteWord = ByteWord.create(bytes);
    }

    @Override
    public int length() {
        return byteWord.size();
    }

    @Override
    public ByteWord get() {
        return byteWord;
    }
}
