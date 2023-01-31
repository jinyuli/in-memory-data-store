package com.codeloam.memory.store.command;

import com.codeloam.memory.store.database.DataType;
import com.codeloam.memory.store.network.ByteWord;

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

    @Override
    public ByteWord getKey() {
        return null;
    }

    @Override
    public List<ByteWord> getValues() {
        return null;
    }

    @Override
    public ByteWord getOption(String optionName) {
        return null;
    }

    @Override
    public boolean hasOption(String optionName) {
        return false;
    }
}
