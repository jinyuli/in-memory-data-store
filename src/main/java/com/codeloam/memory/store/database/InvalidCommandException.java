package com.codeloam.memory.store.database;

import com.codeloam.memory.store.JimdsException;

/**
 * @author jinyu.li
 * @since 1.0
 */
public class InvalidCommandException extends JimdsException {
    public InvalidCommandException(String message) {
        super(message);
    }
}
