package com.codeloam.memory.store.command;

import com.codeloam.memory.store.database.DataType;
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
    private final ByteWord key;

    /**
     * Values for key, may be null, 1, or more than 1.
     */
    private final List<ByteWord> values;
    private final Map<String, ByteWord> options;

    /**
     * Constructor.
     *
     * @param name name of command
     * @param dataType data type
     * @param key key, optional
     * @param values values, optional
     * @param options options, optional
     */
    public SimpleCommand(String name, DataType dataType, ByteWord key,
                         List<ByteWord> values, Map<String, ByteWord> options) {
        super(name, dataType);
        this.key = key;
        this.values = values;
        this.options = options;
    }

    @Override
    public ByteWord getKey() {
        return key;
    }

    @Override
    public ByteWord getOption(String optionName) {
        return options == null ? null : options.get(optionName);
    }

    @Override
    public boolean hasOption(String optionName) {
        return options != null && options.containsKey(optionName);
    }

    @Override
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
