package com.codeloam.memory.store.network;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Reader for client request.
 *
 * @author jinyu.li
 * @since 1.0
 */
public class ClientCommandReader implements CommandReader {
    private static final int DEFAULT_BUF_SIZE = 32;
    private static final byte BYTE_WHITESPACE = ' ';
    private static final byte BYTE_DOUBLE_QUOTE = '"';
    private static final byte BYTE_SINGLE_QUOTE = '\'';
    private static final byte BYTE_BACKSLASH = '\\';
    private final int bufSize;

    /**
     * Init.
     */
    public ClientCommandReader() {
        bufSize = DEFAULT_BUF_SIZE;
    }

    /**
     * Read command from given input stream.
     * This method will not close the input stream.
     * Use whitespace(' ') to split commands, and support quotes.
     *
     * @param stream stream to read bytes from
     * @return split commands
     * @throws IOException if throw by input stream
     */
    public List<ByteWord> read(InputStream stream) throws IOException {
        byte[] buf = new byte[bufSize];
        int count = 0;
        List<byte[]> bufList = new ArrayList<>();
        while (count >= 0) {
            count = stream.read(buf);
            if (count == buf.length) {
                bufList.add(buf);
                buf = new byte[bufSize];
            } else if (count > 0) {
                byte[] array = new byte[count];
                System.arraycopy(buf, 0, array, 0, count);
                bufList.add(array);
            }
        }
        return splitCommand(bufList);
    }

    private List<ByteWord> splitCommand(List<byte[]> bufList) {
        List<ByteWord> words = new ArrayList<>();
        int curBufListIndex = 0;
        int curBufIndex = 0;
        while (curBufListIndex < bufList.size()) {
            Word word = readWithQuote(bufList, curBufListIndex, curBufIndex);
            curBufListIndex = word.bufListIndex;
            curBufIndex = word.bufIndex;
            if (word.hasWord()) {
                words.add(ByteWord.create(word.bytes));
            }
        }
        return words;
    }

    private Word readWithQuote(List<byte[]> bufList, int bufListIndex, int bufIndex) {
        List<byte[]> wordBufList = new ArrayList<>();
        boolean singleQuote = false;
        boolean doubleQuote = false;
        while (bufListIndex < bufList.size()) {
            byte[] buf = bufList.get(bufListIndex);
            int start = bufIndex;
            int end = buf.length;

            if (!singleQuote && !doubleQuote) {
                // should not ignore ' ' inside quotes
                while (start < end && buf[start] == BYTE_WHITESPACE) {
                    ++start;
                }
            }
            if (start >= end) {
                bufListIndex++;
                bufIndex = 0;
                continue;
            }
            int i = start;
            if (doubleQuote || (wordBufList.isEmpty() && buf[i] == BYTE_DOUBLE_QUOTE)) {
                // find next double quote
                if (!doubleQuote) {
                    // pass the first "
                    i++;
                    doubleQuote = true;
                }
                while (i < end
                        && (buf[i] != BYTE_DOUBLE_QUOTE
                            || (buf[i] == BYTE_DOUBLE_QUOTE && buf[i - 1] == BYTE_BACKSLASH))) {
                    ++i;
                }
                if (i < end) {
                    //including the end "
                    i++;
                }
            } else if (singleQuote || (wordBufList.isEmpty() && buf[i] == BYTE_SINGLE_QUOTE)) {
                // find next single quote
                if (!singleQuote) {
                    i++;
                    singleQuote = true;
                }
                while (i < end
                        && (buf[i] != BYTE_SINGLE_QUOTE
                            || (buf[i] == BYTE_SINGLE_QUOTE && buf[i - 1] == BYTE_BACKSLASH))) {
                    ++i;
                }
                if (i < end) {
                    //including the end '
                    i++;
                }
            } else {
                while (i < end && buf[i] != BYTE_WHITESPACE) {
                    ++i;
                }
            }
            if (i > start) {
                if (i < end) {
                    byte[] arr = new byte[i - start];
                    System.arraycopy(buf, start, arr, 0, arr.length);
                    wordBufList.add(arr);
                    return new Word(i, bufListIndex, wordBufList);
                } else {
                    // not the end yet
                    bufListIndex++;
                    bufIndex = 0;
                    if (start == 0 && i == end) {
                        wordBufList.add(buf);
                    } else {
                        byte[] arr = new byte[i - start];
                        System.arraycopy(buf, start, arr, 0, arr.length);
                        wordBufList.add(arr);
                    }
                }
            } else {
                bufListIndex++;
                bufIndex = 0;
            }
        }

        return new Word(bufIndex, bufListIndex, wordBufList);
    }

    /**
     * Represents word position in a byte array.
     */
    static class Word {
        int bufIndex;
        int bufListIndex;
        List<byte[]> bytes;

        public Word(int bufIndex, int bufListIndex, List<byte[]> bytes) {
            this.bufIndex = bufIndex;
            this.bufListIndex = bufListIndex;
            this.bytes = bytes;
        }

        public boolean hasWord() {
            return bytes != null && bytes.size() > 0;
        }
    }
}
