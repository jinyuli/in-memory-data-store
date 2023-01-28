package com.codeloam.memory.store.command;

import com.codeloam.memory.store.network.ByteWord;

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
    private Map<String, CommandConfig> commandMap;

    private CommandFactory() {
        commandMap = new HashMap<>();
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
        String name = words.get(0).getString();
        CommandConfig commandConfig = commandMap.get(name);
        if (commandConfig == null) {
            throw new InvalidCommandException(name);
        }
        return commandConfig.parse(words);
    }
}
