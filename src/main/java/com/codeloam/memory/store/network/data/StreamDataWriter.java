package com.codeloam.memory.store.network.data;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Write data to OutputStream.
 *
 * @author jinyu.li
 * @since 1.0
 */
public class StreamDataWriter implements DataWriter {
    private final OutputStream stream;

    public StreamDataWriter(OutputStream stream) {
        this.stream = stream;
    }

    @Override
    public void write(byte[] buf) throws IOException {
        stream.write(buf);
    }

    @Override
    public void write(byte[] buf, int offset, int size) throws IOException {
        stream.write(buf, offset, size);
    }
}
