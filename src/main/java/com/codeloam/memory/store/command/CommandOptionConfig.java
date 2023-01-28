package com.codeloam.memory.store.command;

import java.util.List;

/**
 * Command option configuration.
 *
 * <p>Each option at least has name, may or may not have a value.
 *
 * @param name          option name
 * @param required      if true, the option is required
 * @param valueRequired if true, option value is required
 * @param valueIsNumber if true, value must be a valid number
 * @param nextOptions   if not null, these options must be shown after current option
 * @author jinyu.li
 * @since 1.0
 */
public record CommandOptionConfig(String name, boolean required, boolean valueRequired,
                                  boolean valueIsNumber, List<CommandOptionConfig> nextOptions) {
}
