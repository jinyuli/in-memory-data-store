package com.codeloam.memory.store;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ThreadFactory with given name pattern.
 *
 * @author jinyu.li
 * @since 1.0
 */
public class NamedThreadFactory implements ThreadFactory {
    private final String prefix;
    private final AtomicInteger count;

    public NamedThreadFactory(String prefix) {
        this.prefix = prefix;
        this.count = new AtomicInteger(0);
    }

    @Override
    public Thread newThread(Runnable r) {
        return new Thread(r, String.format("%s_%d", prefix, count.getAndIncrement()));
    }
}
