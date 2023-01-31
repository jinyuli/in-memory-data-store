package com.codeloam.memory.store.database.result;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Base class for all data types returned to client.
 *
 * @author jinyu.li
 * @since 1.0
 */
public abstract class JimdsResult {
    /**
     * Get result type
     * @return type
     */
    public abstract ResultType getResultType();

    /**
     * Write current result to output stream.
     *
     * @param output output
     */
    public abstract void write(OutputStream output) throws IOException;
}
