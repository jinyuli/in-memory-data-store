package com.codeloam.memory.store.network;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Represent a word with a byte array or a list of byte array.
 *
 * @author jinyu.li
 * @since 1.0
 */
public abstract class ByteWord {
    public static final ByteWord NULL = new ByteWord() {
        @Override
        public int size() {
            return 0;
        }

        @Override
        public byte[] get() {
            return null;
        }

        @Override
        public String getString() {
            return null;
        }

        @Override
        public String toString() {
            return "NULL";
        }
    };

    /**
     * Create an instance of ByteWord with given byte array.
     *
     * <p>throw IllegalArgumentException if the given array is null or empty.
     *
     * @param word byte array
     *
     * @return an instance of ByteWord
     */
    public static ByteWord create(byte[] word) {
        if (word == null || word.length == 0) {
            throw new IllegalArgumentException("empty word");
        }
        return new SingleBytesWord(word);
    }

    /**
     * Create an instance of ByteWord with given byte array.
     *
     * <p>throw IllegalArgumentException if the given list is null or empty.
     *
     * @param word a list of byte arrays
     *
     * @return an instance of ByteWord
     */
    public static ByteWord create(List<byte[]> word) {
        if (word == null || word.isEmpty()) {
            throw new IllegalArgumentException("empty word");
        }
        if (word.size() == 1) {
            return new SingleBytesWord(word.get(0));
        }
        return new MultiBytesWord(word);
    }

    /**
     * Size.
     *
     * @return number of bytes in the word
     */
    public abstract int size();

    /**
     * An array of bytes.
     *
     * @return an array of bytes.
     */
    public abstract byte[] get();

    /**
     * Whether the word is a number, integer or float.
     *
     * @return true if it's a number
     */
    public boolean isNumber() {
        String str = getString();
        return str != null && str.matches("\\d+(\\.\\d+)?");
    }

    /**
     * String representation.
     *
     * @return string
     */
    public abstract String getString();

    private static class SingleBytesWord extends ByteWord {
        private final byte[] word;

        public SingleBytesWord(byte[] word) {
            if (word == null || word.length == 0) {
                throw new IllegalArgumentException("given byte array is null or empty");
            }
            this.word = word;
        }

        @Override
        public int size() {
            return word.length;
        }

        @Override
        public byte[] get() {
            return word;
        }

        @Override
        public String getString() {
            return new String(word);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof SingleBytesWord that)) {
                return false;
            }
            return Arrays.equals(word, that.word);
        }


        @Override
        public int hashCode() {
            return Arrays.hashCode(word);
        }

        @Override
        public String toString() {
            return "SingleBytesWord{"
                    + "word=" + new String(word)
                    + '}';
        }
    }

    private static class MultiBytesWord extends ByteWord {
        private final List<byte[]> word;
        private int length;

        /**
         * Only initialized when needed.
         */
        private byte[] flattenedWord;

        public MultiBytesWord(List<byte[]> word) {
            if (word == null || word.size() == 0) {
                throw new IllegalArgumentException("given list is null or empty");
            }
            this.word = word;
            for (byte[] w : word) {
                length += w.length;
            }
        }

        @Override
        public int size() {
            return length;
        }

        @Override
        public byte[] get() {
            if (flattenedWord == null) {
                flattenedWord = new byte[length];
                int index = 0;
                for (byte[] w : word) {
                    System.arraycopy(w, 0, flattenedWord, index, w.length);
                    index += w.length;
                }
            }
            return flattenedWord;
        }

        @Override
        public String getString() {
            return new String(get());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof MultiBytesWord that)) {
                return false;
            }
            if (word.size() == that.word.size()) {
                for (int i = 0; i < word.size(); ++i) {
                    if (!Arrays.equals(word.get(i), that.word.get(i))) {
                        return false;
                    }
                }
                return true;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hash(word);
        }

        @Override
        public String toString() {
            return "MultiBytesWord{"
                    + "word=" + word.stream().map(String::new).collect(Collectors.joining(","))
                    + '}';
        }
    }
}
