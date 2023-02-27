package com.codeloam.memory.store.network.data;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

/**
 * Write data to WritableByteChannel.
 *
 * @author jinyu.li
 * @since 1.0
 */
public class ChannelDataWriter implements DataWriter {
    private final WritableByteChannel channel;

    public ChannelDataWriter(WritableByteChannel channel) {
        this.channel = channel;
    }

    @Override
    public void write(byte[] buf) throws IOException {
        channel.write(ByteBuffer.wrap(buf));
    }

    @Override
    public void write(byte[] buf, int offset, int size) throws IOException {
        channel.write(ByteBuffer.wrap(buf, offset, size));
    }
}
