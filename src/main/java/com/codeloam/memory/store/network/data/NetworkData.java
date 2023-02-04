package com.codeloam.memory.store.network.data;

import com.codeloam.memory.store.network.ByteWord;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * Base class for all data types transferred between clients and server.
 *
 * @author jinyu.li
 * @since 1.0
 */
public abstract class NetworkData {
    public static final byte[] END = "\r\n".getBytes(StandardCharsets.UTF_8);

    /**
     * Get result type.
     *
     * @return type
     */
    public abstract NetworkDataType getNetworkDataType();

    /**
     * Write current result to output stream.
     *
     * @param output output
     */
    public abstract void write(OutputStream output) throws IOException;

    /**
     * Convenient method for subclass to write a number to output.
     *
     * @param output output
     * @param value value
     * @throws IOException if thrown by output
     */
    protected void write(OutputStream output, long value) throws IOException {
        output.write(String.valueOf(value).getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Convenient method for subclass to write a ByteWord to output.
     *
     * @param output output
     * @param word word
     * @throws IOException if thrown by output
     */
    protected void write(OutputStream output, ByteWord word) throws IOException {
        word.write(output);
    }
}
