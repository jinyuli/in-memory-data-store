package com.codeloam.memory.store.command;

import com.codeloam.memory.store.datastructure.DataType;

import java.util.List;

/**
 * Compound command include more than one commands.
 *
 * @author jinyu.li
 * @since 1.0
 */
public class CompositeCommand extends Command {
    private final List<Command> commands;

    public CompositeCommand(String name, DataType dataType, List<Command> commands) {
        super(name, dataType);
        this.commands = commands;
    }

    public List<Command> getCommands() {
        return commands;
    }
}
