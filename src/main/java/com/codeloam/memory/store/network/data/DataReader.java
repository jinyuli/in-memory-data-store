package com.codeloam.memory.store.network.data;

import java.io.IOException;
import java.util.List;

/**
 * Data reader.
 *
 * @author jinyu.li
 * @since 1.0
 */
public interface DataReader {

    /**
     * Peek one byte.
     *
     * @return the first byte if there is, or null
     * @throws IOException if encounters any I/O exception
     */
    Byte peek() throws IOException;

    /**
     * Skip given bytes.
     *
     * @param bytes number of bytes to skip
     * @throws IOException if encounters any I/O exception
     */
    void skip(int bytes) throws IOException;

    /**
     * Read len bytes.
     *
     * @param len number of bytes to read
     * @return byte array
     * @throws IOException if encounters any I/O exception
     */
    byte[] read(int len) throws IOException;

    /**
     * Read until \r.
     *
     * @return byte array
     * @throws IOException if encounters any I/O exception
     */
    List<byte[]> readUntilStop() throws IOException;
}
