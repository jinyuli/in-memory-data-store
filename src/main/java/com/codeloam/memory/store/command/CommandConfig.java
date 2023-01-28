package com.codeloam.memory.store.command;

import com.codeloam.memory.store.network.ByteWord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                            List<CommandOptionConfig> options) {

    /**
     * Convert given words to a command, set name, key, value  and options if necessary.
     *
     * @param words words
     * @return a command or null
     */
    public Command parse(List<ByteWord> words) {
        // name has been parsed
        int index = 1;
        ByteWord key = null;
        boolean optionParsed = false;
        List<ByteWord> values = null;
        Map<String, ByteWord> optionMap = new HashMap<>();
        if (requireKey) {
            if (index < words.size()) {
                key = words.get(index++);
            } else {
                throw new InvalidCommandException(name, "key required");
            }
        }

        if (supportMultiValues) {
            // parse options first
            optionParsed = true;
            if (index < words.size() && options != null && !options.isEmpty()) {
                index = parseOptions(options, words, index, optionMap);
            }
        }

        if (requireValue && index < words.size()) {
            if (supportMultiValues) {
                values = new ArrayList<>(words.subList(index, words.size()));
                index = words.size();
            } else {
                values = new ArrayList<>(words.subList(index, index + 1));
                index++;
            }
        }

        if (requireValue && values == null) {
            throw new InvalidCommandException(name, "value required");
        }

        if (!optionParsed && index < words.size() && options != null && !options.isEmpty()) {
            parseOptions(options, words, index, optionMap);
        }

        if (options != null && !options.isEmpty()) {
            for (CommandOptionConfig optionConfig : options) {
                if (optionConfig.required() && !optionMap.containsKey(optionConfig.name())) {
                    throw new InvalidCommandException(name(),
                            String.format("option %s is required", optionConfig.name()));
                }
            }
        }

        return new SimpleCommand(name, key, values, optionMap.isEmpty() ? null : optionMap);
    }

    private int parseOptions(List<CommandOptionConfig> optionConfigs, List<ByteWord> words,
                             int index, Map<String, ByteWord> optionMap) {
        while (index < words.size()) {
            ByteWord byteWord = words.get(index);
            String name = byteWord.getString();
            boolean parsed = false;
            for (CommandOptionConfig option : optionConfigs) {
                if (option.name().equals(name)) {
                    index++;
                    index = parseOption(option, words, index, optionMap);
                    parsed = true;
                    break;
                }
            }
            // found an unknown option, what to do?
            if (!parsed) {
                index++;
            }
        }

        return index;
    }

    private int parseOption(CommandOptionConfig optionConfig, List<ByteWord> words,
                            int index, Map<String, ByteWord> optionMap) {
        ByteWord value = ByteWord.NULL;
        if (optionConfig.valueRequired() && index < words.size()) {
            value = words.get(index++);
            if (optionConfig.valueIsNumber() && !value.isNumber()) {
                throw new InvalidCommandException(this.name, String.format("%s should be a number", name));
            }
        }
        if (optionConfig.valueRequired() && value == ByteWord.NULL) {
            throw new InvalidCommandException(name(),
                    String.format("option %s value is required", optionConfig.name()));
        }
        optionMap.put(optionConfig.name(), value);

        if (optionConfig.nextOptions() != null && !optionConfig.nextOptions().isEmpty()) {
            index = parseOptions(optionConfig.nextOptions(), words, index, optionMap);
            for (CommandOptionConfig config : optionConfig.nextOptions()) {
                if (config.required() && !optionMap.containsKey(config.name())) {
                    throw new InvalidCommandException(name(), String.format("option %s is required", config.name()));
                }
            }
        }
        return index;
    }
}
