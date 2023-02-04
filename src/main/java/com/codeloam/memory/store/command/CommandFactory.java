package com.codeloam.memory.store.command;

import com.codeloam.memory.store.database.DataType;
import com.codeloam.memory.store.network.ByteWord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Factory to create command.
 *
 * @author jinyu.li
 * @since 1.0
 */
public class CommandFactory {
    private static CommandFactory singleton;

    private final Map<String, CommandConfig> commandMap;

    private CommandFactory() {
        List<CommandConfig> commandConfigs = List.of(
                new CommandConfig("PING", false, false, false, null, DataType.System),
                new CommandConfig("GET", true, false, false, null, DataType.String),
                new CommandConfig("SET", true, true, false, null, DataType.String)
        );
        commandMap = new HashMap<>();
        for (CommandConfig commandConfig : commandConfigs) {
            commandMap.put(commandConfig.name(), commandConfig);
        }
    }

    public static Command parseCommand(List<ByteWord> words) {
        return getInstance().generate(words);
    }

    /**
     * Get the single instance of CommandFactory.
     *
     * @return instance of CommandFactory
     */
    public static CommandFactory getInstance() {
        if (singleton == null) {
            synchronized (CommandFactory.class) {
                singleton = new CommandFactory();
            }
        }
        return singleton;
    }

    /**
     * Validate and generate a Command.
     *
     * @param words split words from command string
     * @return a command or null if invalid
     */
    public Command generate(List<ByteWord> words) {
        if (words == null || words.size() < 1) {
            throw new IllegalArgumentException("illegal command");
        }

        // currently, only support single command
        String name = words.get(0).getString().toUpperCase();
        CommandConfig commandConfig = commandMap.get(name);
        if (commandConfig == null) {
            throw new InvalidCommandException(name);
        }
        return parse(commandConfig, words);
    }

    /**
     * Convert given words to a command, set name, key, value  and options if necessary.
     *
     * @param words words
     * @return a command or null
     */
    public Command parse(CommandConfig commandConfig, List<ByteWord> words) {
        // name has been parsed
        int index = 1;
        ByteWord key = null;
        boolean optionParsed = false;
        List<ByteWord> values = null;
        Map<String, ByteWord> optionMap = new HashMap<>();
        if (commandConfig.requireKey()) {
            if (index < words.size()) {
                key = words.get(index++);
            } else {
                throw new InvalidCommandException(commandConfig.name(), "key required");
            }
        }

        List<CommandOptionConfig> options = commandConfig.options();

        if (commandConfig.supportMultiValues()) {
            // parse options first
            optionParsed = true;
            if (index < words.size() && options != null && !options.isEmpty()) {
                index = parseOptions(commandConfig, options, words, index, optionMap);
            }
        }

        if (commandConfig.requireValue() && index < words.size()) {
            if (commandConfig.supportMultiValues()) {
                values = new ArrayList<>(words.subList(index, words.size()));
                index = words.size();
            } else {
                values = new ArrayList<>(words.subList(index, index + 1));
                index++;
            }
        }

        if (commandConfig.requireValue() && values == null) {
            throw new InvalidCommandException(commandConfig.name(), "value required");
        }

        if (!optionParsed && index < words.size() && options != null && !options.isEmpty()) {
            parseOptions(commandConfig, options, words, index, optionMap);
        }

        if (options != null && !options.isEmpty()) {
            for (CommandOptionConfig optionConfig : options) {
                if (optionConfig.required() && !optionMap.containsKey(optionConfig.name())) {
                    throw new InvalidCommandException(commandConfig.name(),
                            String.format("option %s is required", optionConfig.name()));
                }
            }
        }

        return new SimpleCommand(commandConfig.name(), commandConfig.dataType(), key, values,
                optionMap.isEmpty() ? null : optionMap);
    }

    private int parseOptions(CommandConfig commandConfig, List<CommandOptionConfig> optionConfigs, List<ByteWord> words,
                             int index, Map<String, ByteWord> optionMap) {
        while (index < words.size()) {
            ByteWord byteWord = words.get(index);
            String name = byteWord.getString();
            boolean parsed = false;
            for (CommandOptionConfig option : optionConfigs) {
                if (option.name().equals(name)) {
                    index++;
                    index = parseOption(commandConfig, option, words, index, optionMap);
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

    private int parseOption(CommandConfig commandConfig, CommandOptionConfig optionConfig, List<ByteWord> words,
                            int index, Map<String, ByteWord> optionMap) {
        ByteWord value = ByteWord.NULL;
        if (optionConfig.valueRequired() && index < words.size()) {
            value = words.get(index++);
            if (optionConfig.valueIsNumber() && !value.isNumber()) {
                throw new InvalidCommandException(commandConfig.name(),
                        String.format("%s should be a number", optionConfig.name()));
            }
        }
        if (optionConfig.valueRequired() && value == ByteWord.NULL) {
            throw new InvalidCommandException(commandConfig.name(),
                    String.format("option %s value is required", optionConfig.name()));
        }
        optionMap.put(optionConfig.name(), value);

        if (optionConfig.nextOptions() != null && !optionConfig.nextOptions().isEmpty()) {
            index = parseOptions(commandConfig, optionConfig.nextOptions(), words, index, optionMap);
            for (CommandOptionConfig config : optionConfig.nextOptions()) {
                if (config.required() && !optionMap.containsKey(config.name())) {
                    throw new InvalidCommandException(commandConfig.name(),
                            String.format("option %s is required", config.name()));
                }
            }
        }
        return index;
    }
}
