package com.codeloam.memory.store.database;

import com.codeloam.memory.store.JimdsException;

/**
 * @author jinyu.li
 * @since 1.0
 */
public class UnknownCommandException extends JimdsException {
    public UnknownCommandException(String message) {
        super(message);
    }
}
