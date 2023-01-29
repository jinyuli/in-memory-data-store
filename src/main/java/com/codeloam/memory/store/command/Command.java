package com.codeloam.memory.store.command;

import com.codeloam.memory.store.datastructure.DataType;

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
