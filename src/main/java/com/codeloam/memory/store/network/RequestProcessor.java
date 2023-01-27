package com.codeloam.memory.store.network;

import java.io.InputStream;

/**
 * Process request from an input stream.
 *
 * <p>Implementations should be thread safe.
 *
 * @author jinyu.li
 * @since 1.0
 */
public interface RequestProcessor {
    void process(InputStream inputStream);
}
