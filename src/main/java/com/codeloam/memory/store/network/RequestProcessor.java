package com.codeloam.memory.store.network;

import com.codeloam.memory.store.command.Command;
import com.codeloam.memory.store.database.Database;
import com.codeloam.memory.store.network.data.NetworkData;

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
     * @param command command
     * @return command result
     * @throws IOException only throws when writing data to output
     */
    NetworkData process(Database database, Command command) throws IOException;
}
