package com.codeloam.memory.store.network;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represent a word with a byte array or a list of byte array.
 *
 * @author jinyu.li
 * @since 1.0
 */
public abstract class ByteWord implements Comparable<ByteWord> {
    private static final String PATTERN_LONG = "^\\d+$";

    /**
     * Create an instance of ByteWord with given string.
     *
     * <p>throw IllegalArgumentException if the given array is null or empty.
     *
     * @param word string
     * @return an instance of ByteWord
     */
    public static ByteWord create(String word) {
        if (word == null || word.length() == 0) {
            throw new IllegalArgumentException("empty word");
        }
        return new SingleByteWord(word.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Create an instance of ByteWord with given byte array.
     *
     * <p>throw IllegalArgumentException if the given array is null or empty.
     *
     * @param word byte array
     * @return an instance of ByteWord
     */
    public static ByteWord create(byte[] word) {
        if (word == null || word.length == 0) {
            throw new IllegalArgumentException("empty word");
        }
        return new SingleByteWord(word);
    }

    /**
     * Create an instance of ByteWord with given byte array.
     *
     * <p>throw IllegalArgumentException if the given list is null or empty.
     *
     * @param word a list of byte arrays
     * @return an instance of ByteWord
     */
    public static ByteWord create(List<byte[]> word) {
        if (word == null || word.isEmpty()) {
            throw new IllegalArgumentException("empty word");
        }
        if (word.size() == 1) {
            return new SingleByteWord(word.get(0));
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

    abstract byte getByte(int i);

    /**
     * Whether the word is a number, integer or float.
     *
     * @return true if it's a number
     */
    public boolean isNumber() {
        String str = getString();
        return str != null && str.matches("\\d+");
    }

    /**
     * Return number, only valid if isNumber() is true.
     *
     * @return number
     */
    public long getNumber() {
        String str = getString();
        if (str.matches(PATTERN_LONG)) {
            return Long.parseLong(str);
        }
        return 0;
    }

    /**
     * String representation.
     *
     * @return string
     */
    public abstract String getString();

    /**
     * Write data to output stream.
     *
     * @param output output
     * @throws IOException
     */
    public abstract void write(OutputStream output) throws IOException;

    /**
     * Whether two ByteWord are the same.
     * Note that hashCode must be rewritten.
     *
     * @param o another
     * @return true if have same byte sequence
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ByteWord that)) {
            return false;
        }
        if (size() == that.size()) {
            for (int i = 0; i < size(); ++i) {
                if (getByte(i) != that.getByte(i)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public int compareTo(ByteWord o) {
        ByteWord a = this;
        if (a == o) {
            return 0;
        }
        if (o == null) {
            return 1;
        }
        int minSize = Math.min(size(), o.size());
        for (int i = 0; i < minSize; ++i) {
            byte tb = getByte(i);
            byte ob = o.getByte(i);
            if (tb != ob) {
                return Byte.compare(tb, ob);
            }
        }
        return a.size() - o.size();
    }

    private static class NumberByteWord extends ByteWord {
        private final long value;
        private final byte[] bytes;

        public NumberByteWord(long value) {
            this.value = value;
            bytes = String.valueOf(value).getBytes(StandardCharsets.UTF_8);
        }

        @Override
        public boolean isNumber() {
            return true;
        }

        @Override
        public long getNumber() {
            return value;
        }

        @Override
        public int size() {
            return bytes.length;
        }

        @Override
        public byte[] get() {
            return bytes;
        }

        @Override
        byte getByte(int i) {
            return bytes[i];
        }

        @Override
        public String getString() {
            return new String(bytes);
        }

        @Override
        public int compareTo(ByteWord o) {
            ByteWord a = this;
            if (a == o) {
                return 0;
            }
            if (o == null) {
                return 1;
            }
            if (o.isNumber()) {
                return Long.compare(value, o.getNumber());
            }
            return super.compareTo(o);
        }

        @Override
        public void write(OutputStream output) throws IOException {
            output.write(bytes);
        }
    }

    private static class SingleByteWord extends ByteWord {
        private final byte[] word;

        public SingleByteWord(byte[] word) {
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
        byte getByte(int i) {
            return word[i];
        }

        @Override
        public String getString() {
            return new String(word);
        }

        @Override
        public void write(OutputStream output) throws IOException {
            output.write(word);
        }

        @Override
        public int hashCode() {
            int result = 1;
            for (byte element : word)
                result = 31 * result + element;

            return result;
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
        byte getByte(int i) {
            for (byte[] array : word) {
                if (i < array.length) {
                    return array[i];
                }
                i -= array.length;
            }
            throw new IndexOutOfBoundsException(i);
        }

        @Override
        public String getString() {
            return new String(get());
        }

        @Override
        public void write(OutputStream output) throws IOException {
            for (byte[] array: word) {
                output.write(array);
            }
        }

        /**
         * To make sure that SingleByteWord and MultiBytesWord generate same hash code if they have same byte sequence.
         * <p> for example:
         * SingleByteWord: ['a','b']
         * MultiBytesWord: List.of(['a'],['b'])
         *
         * @return hash code
         */
        @Override
        public int hashCode() {
            int result = 1;
            for (byte[] array : word) {
                for (byte element : array) {
                    result = 31 * result + element;
                }
            }

            return result;
        }

        @Override
        public String toString() {
            return "MultiBytesWord{"
                    + "word=" + word.stream().map(String::new).collect(Collectors.joining(","))
                    + '}';
        }
    }

    public static final ByteWord NULL = new ByteWord() {
        @Override
        public int compareTo(ByteWord o) {
            if (o.size() == 0) {
                return 0;
            }
            return -1;
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public byte[] get() {
            return null;
        }

        @Override
        byte getByte(int i) {
            return 0;
        }

        @Override
        public String getString() {
            return null;
        }

        @Override
        public void write(OutputStream output) throws IOException {
        }

        @Override
        public String toString() {
            return "NULL";
        }
    };
}
