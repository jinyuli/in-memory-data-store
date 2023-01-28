package com.codeloam.memory.store.command;

import com.codeloam.memory.store.network.ByteWord;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a single command.
 *
 * @author jinyu.li
 * @since 1.0
 */
public class SimpleCommand extends Command {
    /**
     * may be null.
     */
    private ByteWord key;

    /**
     * Values for key, may be null, 1, or more than 1.
     */
    private List<ByteWord> values;
    private Map<String, ByteWord> options;

    /**
     * Constructor.
     *
     * @param name name of command
     * @param key key, optional
     * @param values values, optional
     * @param options options, optional
     */
    public SimpleCommand(String name, ByteWord key, List<ByteWord> values, Map<String, ByteWord> options) {
        super(name);
        this.key = key;
        this.values = values;
        this.options = options;
    }

    public ByteWord getKey() {
        return key;
    }

    public List<ByteWord> getValues() {
        return values;
    }

    public Map<String, ByteWord> getOptions() {
        return options;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SimpleCommand that)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        return Objects.equals(key, that.key)
                && Objects.equals(values, that.values)
                && Objects.equals(options, that.options);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), key, values, options);
    }

    @Override
    public String toString() {
        return "SimpleCommand{"
                + "name='" + name
                + ", key=" + key
                + ", values=" + values
                + ", options=" + options + '\''
                + '}';
    }
}
