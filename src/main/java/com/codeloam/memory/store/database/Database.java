package com.codeloam.memory.store.database;

import com.codeloam.memory.store.command.Command;
import com.codeloam.memory.store.network.data.NetworkData;

/**
 * Database interface that is used to manager in-memory data.
 *
 * @author jinyu.li
 * @since 1.0
 */
public interface Database {
    /**
     * Execute a command.
     *
     * @param command command
     * @return result, should not be null
     */
    NetworkData execute(Command command);
}
