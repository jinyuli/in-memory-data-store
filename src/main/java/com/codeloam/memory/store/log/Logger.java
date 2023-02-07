package com.codeloam.memory.store.log;

/**
 * Logger.
 *
 * @author jinyu.li
 * @since 1.0
 */
public class Logger {
    public void log(String format, Object... args) {
        System.out.printf(format, args);
    }

    public void debug(String format, Object... args) {
        log(format, args);
    }

    public void info(String format, Object... args) {
        log(format, args);
    }

    public void warn(String format, Object... args) {
        log(format, args);
    }

    public void error(String format, Object... args) {
        log(format, args);
    }
}
