package com.codeloam.memory.store.command;

import com.codeloam.memory.store.datastructure.DataType;

import java.util.List;

/**
 * Represent a command configuration.
 * It's used to parse a command string.
 *
 * <p>Each command at least has a name, currently it's considered global unique.
 *
 * <p>For commands with multiple values, if there is any option, options should next to key, and before values.
 *
 * @param name               command name, required
 * @param requireKey         if true, key must exist, if false, there should be no key or value
 * @param requireValue       if true, value must exist, if false, there should be no value
 * @param supportMultiValues if true, accept multiple values, separated by whitespace
 * @param options            options for command, optional
 * @author jinyu.li
 * @since 1.0
 */
public record CommandConfig(String name, boolean requireKey, boolean requireValue, boolean supportMultiValues,
                            List<CommandOptionConfig> options, DataType dataType) {
}
