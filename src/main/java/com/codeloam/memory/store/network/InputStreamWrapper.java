package com.codeloam.memory.store.network;

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
public class InputStreamWrapper {
    private static Logger logger = new Logger();
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
     * @param bufSize buffer size
     * @param maxReadSize maximum bytes to read from input stream
     */
    public InputStreamWrapper(InputStream inputStream, int bufSize, int maxReadSize) {
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
    public Byte peekOneByte() throws IOException {
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
            readData();
        }
    }

    /**
     * Read len bytes.
     *
     * @param len number of bytes to read
     * @return a list
     * @throws IOException if thrown by input stream
     */
    public List<byte[]> read(int len) throws IOException {
        List<byte[]> result = new ArrayList<>();
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
            var tmp = new byte[size];
            System.arraycopy(buf, offset, tmp, 0, tmp.length);
            result.add(tmp);
            len -= tmp.length;
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
//            logger.debug("read data, offset:%d, last count:%d, new count:%d, total:%d\n", offset, last, count, totalSize);
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
