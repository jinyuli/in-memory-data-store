package com.codeloam.memory.store.util;

import com.codeloam.memory.store.network.ByteWord;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility for Test.
 *
 * @author jinyu.li
 * @since 1.0
 */
public class ByteWordFactory {

    /**
     * Create ByteWord from given string.
     *
     * @param word string
     * @return ByteWord
     */
    public static ByteWord getByteWord(String word) {
        byte[] wordBytes = word.getBytes(StandardCharsets.UTF_8);
        if (wordBytes.length < 32) {
            return ByteWord.create(word.getBytes(StandardCharsets.UTF_8));
        }
        return getMultiByteWord(word);
    }

    public static ByteWord getByteWord(long word) {
        return ByteWord.create(word);
    }

    /**
     * Create MultiByteWorld from given string.
     *
     * @param word string
     * @return MultiByteWorld
     */
    public static ByteWord getMultiByteWord(String word) {
        List<byte[]> bytes = new ArrayList<>();
        byte[] wordBytes = word.getBytes(StandardCharsets.UTF_8);
        for (int i = 0; i < wordBytes.length; i += 32) {
            int end = i + 32;
            if (end > wordBytes.length) {
                end = wordBytes.length;
            }
            byte[] sub = new byte[(end - i)];
            System.arraycopy(wordBytes, i, sub, 0, sub.length);
            bytes.add(sub);
        }
        return ByteWord.create(bytes);
    }
}
