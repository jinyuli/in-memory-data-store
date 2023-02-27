package com.codeloam.memory.store.measure;

import java.util.HashMap;
import java.util.Map;

/**
 * Store some data.
 *
 * @author jinyu.li
 * @since 1.0
 */
public class MeasureData {
    private final Map<String, Double> data = new HashMap<>();

    public void add(String name) {
        data.put(name, System.currentTimeMillis() * 1.0);
    }

    public void add(String name, long value) {
        data.put(name, value * 1.0);
    }

    public void add(String name, double value) {
        data.put(name, value);
    }

    public Double get(String name) {
        return data.get(name);
    }
}
