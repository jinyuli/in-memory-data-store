package com.codeloam.memory.store.measure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Abstract measure collector, used to collect data.
 *
 * @author jinyu.li
 * @since 1.0
 */
public abstract class MeasureCollector {
    protected final String name;
    protected final List<MeasureData> dataList = new ArrayList<>();

    public MeasureCollector(String name) {
        this.name = name;
    }

    public void add(MeasureData data) {
        dataList.add(data);
    }

    public void addAll(Collection<MeasureData> dataCollection) {
        dataList.addAll(dataCollection);
    }

    public List<MeasureData> getData() {
        return dataList;
    }

    /**
     * Print measure data.
     */
    public abstract void print();
}
