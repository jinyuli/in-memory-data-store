package com.codeloam.memory.store.command;

/**
 * Exception for invalid command.
 *
 * @author jinyu.li
 * @since 1.0
 */
public class InvalidCommandException extends RuntimeException {
    public InvalidCommandException(String commandName) {
        super(String.format("Invalid command %s", commandName));
    }

    public InvalidCommandException(String commandName, String message) {
        super(String.format("Invalid command %s: %s", commandName, message));
    }
}
