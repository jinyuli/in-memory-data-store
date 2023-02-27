package com.codeloam.memory.store.network;

import com.codeloam.memory.store.network.data.DataReader;

import java.io.IOException;
import java.util.List;

/**
 * Read command from input stream, and split it.
 *
 * <p>Implementations should be thread safe.
 *
 * @author jinyu.li
 * @since 1.0
 */
public interface CommandReader {
    /**
     * Read command from given input stream.
     * This method will not close the input stream.
     *
     * @param reader read bytes from
     * @return split commands
     * @throws IOException if throw by input stream
     */
    List<ByteWord> read(DataReader reader) throws IOException;
}
