package com.codeloam.memory.store.network.data;

import com.codeloam.memory.store.command.InvalidCommandException;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * Read data from ReadableByteChannel.
 *
 * @author jinyu.li
 * @since 1.0
 */
public class ChannelDataReader implements DataReader {
    private final ReadableByteChannel channel;
    private final ByteBuffer buf;
    private final byte[] bytes;
    private int offset;
    private int count;
    private int totalSize;
    private final int maxReadSize;

    /**
     * Constructor.
     *
     * @param channel     channel to read data
     * @param bufSize     buffer size
     * @param maxReadSize max bytes to read
     */
    public ChannelDataReader(ReadableByteChannel channel, int bufSize, int maxReadSize) {
        this.channel = channel;
        this.maxReadSize = maxReadSize;
        bytes = new byte[bufSize];
        buf = ByteBuffer.wrap(bytes);
        buf.clear();
    }

    @Override
    public Byte peek() throws IOException {
        readData();
        if (count < 0) {
            // no more data
            return null;
        }
        return bytes[offset];
    }

    @Override
    public void skip(int bytes) throws IOException {
        offset += bytes;
        // if offset == count, then that may be the last byte,
        // so no need to read more data.
        while (offset > count) {
            buf.clear();
            int last = count;
            count = channel.read(buf);
            offset -= last;
            buf.flip();
            if (count < 0) {
                break;
            }
            totalSize += count;
            if (totalSize > maxReadSize) {
                throw new InvalidCommandException("The command is too long");
            }
        }
    }

    @Override
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
            System.arraycopy(bytes, offset, result, index, size);
            index += size;
            len -= size;
            offset += size;
        }
        return result;
    }

    @Override
    public List<byte[]> readUntilStop() throws IOException {
        List<byte[]> result = new ArrayList<>();
        while (true) {
            readData();
            if (count < 0) {
                // no more data
                break;
            }
            int index = offset;
            while (index < count && bytes[index] != '\r') {
                index++;
            }
            if (index > offset) {
                var tmp = new byte[index - offset];
                System.arraycopy(bytes, offset, tmp, 0, tmp.length);
                result.add(tmp);
            }
            offset = index;
            if (index < count) {
                break;
            }
        }
        return result;
    }

    public int getOffset() {
        return offset;
    }

    public int getCount() {
        return count;
    }

    /**
     * Read more data from channel.
     *
     * @throws IOException if thrown by channel
     */
    private void readData() throws IOException {
        while (offset >= count) {
            buf.clear();
            int last = count;
            count = channel.read(buf);
            offset -= last;
            buf.flip();
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
