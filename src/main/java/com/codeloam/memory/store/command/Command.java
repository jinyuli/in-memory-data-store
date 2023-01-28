package com.codeloam.memory.store.command;

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

    public Command(String name) {
        this.name = name;
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
