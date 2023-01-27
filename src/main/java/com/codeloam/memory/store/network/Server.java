package com.codeloam.memory.store.network;

/**
 * Server interface for the in-memory data store.
 *
 * @author jinyu.li
 * @since 1.0
 */
public interface Server extends AutoCloseable {
    /**
     * Start the server.
     */
    void start();
}
