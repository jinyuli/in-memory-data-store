package com.codeloam.memory.store;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jinyu.li
 * @since 1.0
 */
public class MeasureData {
    private final Map<String, Long> data = new HashMap<>();

    public void add(String name, long value) {
        data.put(name, value);
    }

    public Long get(String name) {
        return data.get(name);
    }
}
