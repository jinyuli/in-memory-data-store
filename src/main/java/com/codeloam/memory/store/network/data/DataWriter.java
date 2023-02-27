package com.codeloam.memory.store.network.data;

import java.io.IOException;

/**
 * Data writer.
 *
 * @author jinyu.li
 * @since 1.0
 */
public interface DataWriter {
    /**
     * Write all bytes in buf.
     *
     * @param buf buffer
     * @throws IOException if encounters any I/O exception
     */
    void write(byte[] buf) throws IOException;

    /**
     * Write bytes in buf.
     *
     * @param buf buffer
     * @param offset offset
     * @param size size
     * @throws IOException if encounters any I/O exception
     */
    void write(byte[] buf, int offset, int size) throws IOException;
}
