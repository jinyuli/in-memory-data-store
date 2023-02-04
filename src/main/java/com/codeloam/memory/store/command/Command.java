package com.codeloam.memory.store.command;

import com.codeloam.memory.store.database.DataType;
import com.codeloam.memory.store.network.ByteWord;

import java.util.List;
import java.util.Objects;

/**
 * Represent a command.
 *
 * @author jinyu.li
 * @since 1.0
 */
public abstract class Command {
    /**
     * like GET, SET, .etc.
     * required
     */
    protected String name;
    protected DataType dataType;

    public Command(String name, DataType dataType) {
        this.name = name;
        this.dataType = dataType;
    }

    /**
     * Get command key.
     *
     * @return key or null
     */
    public abstract ByteWord getKey();

    /**
     * Get command value.
     *
     * @return value or null
     */
    public abstract List<ByteWord> getValues();

    /**
     * Get command option value.
     *
     * @param optionName option name
     *
     * @return option value if exists, or null
     */
    public abstract ByteWord getOption(String optionName);

    /**
     * Whether the command has an option with given name.
     *
     * @param optionName option name
     *
     * @return true if has
     */
    public abstract boolean hasOption(String optionName);

    public DataType getDataType() {
        return dataType;
    }

    public String getName() {
        return name;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Command command)) {
            return false;
        }
        return Objects.equals(name, command.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
