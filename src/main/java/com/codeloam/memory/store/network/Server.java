package com.codeloam.memory.store.network;

/**
 * Server interface for the in-memory data store.
 *
 * @author jinyu.li
 * @since 1.0
 */
public interface Server extends AutoCloseable {
    int DEFAULT_BUF_SIZE = 32;
    int MAX_COMMAND_LENGTH = 512 * 1024 * 1024 + 1024 * 1024;
    /**
     * Start the server.
     */
    void start();
}
