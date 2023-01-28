package com.codeloam.memory.store.command;

/**
 * Compound command include more than one commands.
 *
 * @author jinyu.li
 * @since 1.0
 */
public class CompoundCommand extends Command {

    public CompoundCommand(String name) {
        super(name);
    }
}
