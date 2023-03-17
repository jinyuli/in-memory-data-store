package com.codeloam.memory.store.network.nio;

import java.util.concurrent.ThreadFactory;

/**
 * ThreadFactory.
 *
 * @author jinyu.li
 * @since 1.0
 */
public class NamedThreadFactory implements ThreadFactory {
    private final String prefix;
    private int count;

    public NamedThreadFactory(String prefix) {
        this.prefix = prefix;
        this.count = 1;
    }

    private String getNextName() {
        return String.format("%s_%d", prefix, count++);
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r, getNextName());
        return thread;
    }
}
