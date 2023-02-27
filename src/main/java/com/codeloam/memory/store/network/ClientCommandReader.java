package com.codeloam.memory.store.network;

import com.codeloam.memory.store.command.InvalidCommandException;
import com.codeloam.memory.store.network.data.DataReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Reader for client request.
 *
 * @author jinyu.li
 * @since 1.0
 */
public class ClientCommandReader implements CommandReader {
    private static final byte BYTE_WHITESPACE = ' ';
    private static final byte BYTE_DOUBLE_QUOTE = '"';
    private static final byte BYTE_SINGLE_QUOTE = '\'';
    private static final byte BYTE_BACKSLASH = '\\';


    /**
     * Init.
     */
    public ClientCommandReader() {
    }

    /**
     * Read command from given input stream.
     * This method will not close the input stream.
     * Use whitespace(' ') to split commands, and support quotes.
     *
     * @param reader read bytes from
     * @return split commands
     * @throws IOException if throw by input stream
     */
    public List<ByteWord> read(DataReader reader) throws IOException {
        Byte prefix = reader.peek();
        if (prefix == null) {
            throw new InvalidCommandException("no command");
        }
        switch (prefix) {
            case '$', '-', '+', '*', ':' -> {
                return readResp(reader, prefix);
            }
            default -> {
                List<byte[]> bufList = reader.readUntilStop();
                return splitCommand(bufList);
            }
        }
    }

    /**
     * Read RESP format.
     *
     * @param inputWrapper input
     * @param prefix       the first byte
     * @return a list
     * @throws IOException if thrown by input
     */
    private List<ByteWord> readResp(DataReader inputWrapper, byte prefix) throws IOException {
        // skip the first byte
        inputWrapper.skip(1);
        switch (prefix) {
            case '*':
                return readArray(inputWrapper);
            case '$':
                return readBulkString(inputWrapper);
            case ':':
                List<ByteWord> words = readSimpleString(inputWrapper);
                ByteWord word = words.get(0);
                long value = Long.parseLong(word.getString());
                return List.of(ByteWord.create(value));
            case '+', '-':
                return readSimpleString(inputWrapper);
            default:
                throw new InvalidCommandException("Unknown sign:" + prefix);
        }
    }

    /**
     * Array format.
     * "*0\r\n"
     * "*2\r\n$5\r\nhello\r\n$5\r\nworld\r\n"
     * "*3\r\n:1\r\n:2\r\n:3\r\n"
     *
     * @param input input
     * @return a list
     * @throws IOException if thrown by input
     */
    private List<ByteWord> readArray(DataReader input) throws IOException {
        List<byte[]> bufList = input.readUntilStop();

        ByteWord lengthWord = ByteWord.create(bufList);
        int size = Integer.parseInt(lengthWord.getString());
        if (size == -1) {
            // TODO client should not send NULL bulk string?
            List<ByteWord> result = new ArrayList<>();
            result.add(null);
            return result;
        } else if (size == 0) {
            return List.of();
        }

        input.skip(2);

        List<ByteWord> result = new ArrayList<>();
        while (size > 0) {
            Byte prefix = input.peek();
            switch (prefix) {
                case '$', '-', '+', '*', ':' -> {
                    result.addAll(readResp(input, prefix));
                    input.skip(2);
                }
                default -> throw new InvalidCommandException("invalid resp format: " + prefix);
            }
            size--;
        }
        return result;
    }


    /**
     * BulkString has the following format.
     * "$5\r\nhello\r\n"
     * "$0\r\n\r\n"
     * "$-1\r\n"
     *
     * @param input input
     * @return a list
     * @throws IOException if thrown by input
     */
    private List<ByteWord> readBulkString(DataReader input) throws IOException {
        List<byte[]> bufList = input.readUntilStop();
        input.skip(2);

        ByteWord lengthWord = ByteWord.create(bufList);
        int size = Integer.parseInt(lengthWord.getString());
        if (size == -1) {
            // TODO client should not send NULL bulk string?
            List<ByteWord> result = new ArrayList<>();
            result.add(null);
            return result;
        } else if (size == 0) {
            return List.of();
        }

        byte[] buf = input.read(size);
        return List.of(ByteWord.create(buf));
    }

    /**
     * Read until '\r'.
     *
     * @param input input
     * @return a list
     * @throws IOException if thrown by input
     */
    private List<ByteWord> readSimpleString(DataReader input) throws IOException {
        List<byte[]> bufList = input.readUntilStop();
        return List.of(ByteWord.create(bufList));
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
