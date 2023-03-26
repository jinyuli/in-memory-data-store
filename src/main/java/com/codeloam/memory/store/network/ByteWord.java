package com.codeloam.memory.store.network;

import com.codeloam.memory.store.network.data.DataWriter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Represent a word with a byte array or a list of byte array.
 *
 * @author jinyu.li
 * @since 1.0
 */
public abstract class ByteWord implements Comparable<ByteWord> {
    private static final String PATTERN_LONG = "^\\d+$";
    private static final String PATTERN_DOUBLE = "^\\d+(\\.\\d+)?$";

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
     * Create an instance of ByteWord with given double.
     *
     * @param word string
     * @return an instance of ByteWord
     */
    public static ByteWord create(double word) {
        return new DoubleByteWord(word);
    }

    /**
     * Create an instance of ByteWord with given long.
     *
     * @param word string
     * @return an instance of ByteWord
     */
    public static ByteWord create(long word) {
        return new NumberByteWord(word);
    }

    /**
     * Create an instance of ByteWord with given byte array.
     *
     * <p>throw IllegalArgumentException if the given array is null.
     *
     * @param word byte array
     * @return an instance of ByteWord
     */
    public static ByteWord create(byte[] word) {
        if (word == null) {
            throw new IllegalArgumentException("world is null");
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
        return str != null && str.matches(PATTERN_DOUBLE);
    }

    /**
     * Whether the word is a long.
     *
     * @return true if it's a long
     */
    public boolean isLong() {
        String str = getString();
        return str != null && str.matches(PATTERN_LONG);
    }

    /**
     * Whether the word is a double.
     *
     * @return true if it's a double
     */
    public boolean isDouble() {
        String str = getString();
        return str != null && str.matches(PATTERN_DOUBLE);
    }

    /**
     * Return a long value, only valid if isLong() is true.
     *
     * @return number
     */
    public long getLong() {
        String str = getString();
        return Long.parseLong(str);
    }

    /**
     * Return a double value, only valid if isDouble() is true.
     *
     * @return number
     */
    public double getDouble() {
        String str = getString();
        return Double.parseDouble(str);
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
     * @param writer writer
     * @throws IOException if thrown by output
     */
    public abstract void write(DataWriter writer) throws IOException;

    /**
     * Used to compact data as much as possible.
     * Generally called before save to database.
     *
     * @return a compacted data
     */
    public ByteWord compact() {
        return this;
    }

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
        public boolean isLong() {
            return true;
        }

        @Override
        public boolean isDouble() {
            return false;
        }

        @Override
        public long getLong() {
            return value;
        }

        @Override
        public double getDouble() {
            return 0;
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
        public String toString() {
            return "NumberByteWord{"
                    + "value=" + value
                    + '}';
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }

        @Override
        public void write(DataWriter writer) throws IOException {
            writer.write(bytes);
        }
    }

    private static class DoubleByteWord extends ByteWord {
        private final double value;
        private final byte[] bytes;

        public DoubleByteWord(double value) {
            this.value = value;
            bytes = String.valueOf(value).getBytes(StandardCharsets.UTF_8);
        }

        @Override
        public boolean isLong() {
            return false;
        }

        @Override
        public boolean isDouble() {
            return true;
        }

        @Override
        public long getLong() {
            return 0;
        }

        @Override
        public double getDouble() {
            return value;
        }

        @Override
        public boolean isNumber() {
            return true;
        }

        @Override
        public long getNumber() {
            return 0;
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
                return Double.compare(value, o.getNumber());
            }
            return super.compareTo(o);
        }

        @Override
        public String toString() {
            return "DoubleByteWord{"
                    + "value=" + value
                    + '}';
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }

        @Override
        public void write(DataWriter writer) throws IOException {
            writer.write(bytes);
        }
    }

    private static class SingleByteWord extends ByteWord {
        private final byte[] word;

        public SingleByteWord(byte[] word) {
            if (word == null) {
                throw new IllegalArgumentException("given byte array is null");
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
        public void write(DataWriter writer) throws IOException {
            writer.write(word);
        }

        @Override
        public int hashCode() {
            int result = 1;
            for (byte element : word) {
                result = 31 * result + element;
            }

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
        public void write(DataWriter writer) throws IOException {
            for (byte[] array : word) {
                writer.write(array);
            }
        }

        @Override
        public ByteWord compact() {
            byte[] bytes = new byte[size()];
            int index = 0;
            for (byte[] b : word) {
                System.arraycopy(b, 0, bytes, index, b.length);
                index += b.length;
            }
            return new SingleByteWord(bytes);
        }

        /**
         * To make sure that SingleByteWord and MultiBytesWord generate same hash code if they have same byte sequence.
         *
         * <p>For example:
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
        public void write(DataWriter writer) throws IOException {
        }

        @Override
        public String toString() {
            return "NULL";
        }
    };
}
