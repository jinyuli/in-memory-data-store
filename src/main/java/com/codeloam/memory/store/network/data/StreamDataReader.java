package com.codeloam.memory.store.network.data;

import com.codeloam.memory.store.command.InvalidCommandException;
import com.codeloam.memory.store.log.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * An helper class to read data from InputStream.
 *
 * @author jinyu.li
 * @since 1.0
 */
public class StreamDataReader implements DataReader {
    private static final Logger logger = new Logger();
    private final InputStream inputStream;
    private final byte[] buf;
    private int offset;
    private int count;
    private int totalSize;
    private final int maxReadSize;

    /**
     * Constructor.
     *
     * @param inputStream input
     * @param bufSize     buffer size
     * @param maxReadSize maximum bytes to read from input stream
     */
    public StreamDataReader(InputStream inputStream, int bufSize, int maxReadSize) {
        buf = new byte[bufSize];
        this.inputStream = inputStream;
        this.maxReadSize = maxReadSize;
    }

    /**
     * Read one byte.
     *
     * @return a byte or null
     * @throws IOException if thrown by input stream
     */
    public Byte peek() throws IOException {
        readData();
        if (count < 0) {
            // no more data
            return null;
        }
        return buf[offset];
    }

    /**
     * skip some bytes.
     *
     * @param bytes number of bytes to skip
     */
    public void skip(int bytes) throws IOException {
        offset += bytes;
        // if offset == count, then that may be the last byte,
        // so no need to read more data.
        while (offset > count) {
            int last = count;
            count = inputStream.read(buf);
            offset -= last;
            if (count < 0) {
                break;
            }
            totalSize += count;
            if (totalSize > maxReadSize) {
                throw new InvalidCommandException("The command is too long");
            }
        }
    }

    /**
     * Read len bytes.
     *
     * @param len number of bytes to read
     * @return a list
     * @throws IOException if thrown by input stream
     */
    public byte[] read(int len) throws IOException {
        byte[] result = new byte[len];
        int index = 0;
        while (len > 0) {
            readData();
            if (count < 0) {
                // no more data
                break;
            }
            int size = count - offset;
            if (size > len) {
                size = len;
            }
            System.arraycopy(buf, offset, result, index, size);
            index += size;
            len -= size;
            offset += size;
        }
        return result;
    }

    /**
     * Read bytes until encounters '\r'.
     *
     * @return a list
     * @throws IOException if thrown by input stream
     */
    public List<byte[]> readUntilStop() throws IOException {
        List<byte[]> result = new ArrayList<>();
        while (true) {
            readData();
            if (count < 0) {
                // no more data
                break;
            }
            int index = offset;
            while (index < count && buf[index] != '\r') {
                index++;
            }
            var tmp = new byte[index - offset];
            System.arraycopy(buf, offset, tmp, 0, tmp.length);
            result.add(tmp);
            offset = index;
            if (index < count) {
                break;
            }
        }
        return result;
    }

    /**
     * Skip stop sign, this method is for RESP format, the stop sign is \r\n.
     *
     * @throws IOException if thrown by input stream
     */
    public void skipStopSign() throws IOException {
        // offset should point to '\r', or offset == count
        // if need to read more data, the following operations will call readData().
        // if call skip(2) at here, and current is at last two bytes, readData() may get stuck
        // as client will not send more data.
        offset += 2;
    }

    /**
     * Read more data from input stream.
     *
     * @throws IOException if thrown by input stream
     */
    private void readData() throws IOException {
        while (offset >= count) {
            int last = count;
            count = inputStream.read(buf);
            offset -= last;
            if (count < 0) {
                break;
            }
            totalSize += count;
            if (totalSize > maxReadSize) {
                throw new InvalidCommandException("The command is too long");
            }
        }
    }
}
