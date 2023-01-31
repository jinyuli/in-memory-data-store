package com.codeloam.memory.store.network;

import com.codeloam.memory.store.database.Database;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Process request from an input stream.
 *
 * <p>Implementations should be thread safe.
 *
 * @author jinyu.li
 * @since 1.0
 */
public interface RequestProcessor {
    /**
     * Process requests.
     *
     * @param database current database
     * @param inputStream input stream
     * @param outputStream output stream
     * @throws IOException only throws when writing data to output
     */
    void process(Database database, InputStream inputStream, OutputStream outputStream) throws IOException;
}
