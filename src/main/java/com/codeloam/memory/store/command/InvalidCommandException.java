package com.codeloam.memory.store.command;

import com.codeloam.memory.store.JimdsException;

/**
 * Exception for invalid command.
 *
 * @author jinyu.li
 * @since 1.0
 */
public class InvalidCommandException extends JimdsException {
    public InvalidCommandException(String commandName) {
        super(String.format("Invalid command %s", commandName));
    }

    public InvalidCommandException(String commandName, String message) {
        super(String.format("Invalid command %s: %s", commandName, message));
    }
}
