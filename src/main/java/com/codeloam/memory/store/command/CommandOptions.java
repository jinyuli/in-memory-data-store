package com.codeloam.memory.store.command;

import com.codeloam.memory.store.network.ByteWord;

import java.util.List;

/**
 * Command options.
 *
 * @author jinyu.li
 * @since 1.0
 */
public record CommandOptions(String main, List<CommandOptionConfig> options) {
    public boolean isValid(List<ByteWord> words) {
        return true;
    }
}
