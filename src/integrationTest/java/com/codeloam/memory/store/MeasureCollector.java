package com.codeloam.memory.store;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jinyu.li
 * @since 1.0
 */
public class MeasureCollector {
    private final List<MeasureData> dataList = new ArrayList<>();

    public void add(MeasureData data) {
        dataList.add(data);
    }

    public List<MeasureData> getDataList() {
        return dataList;
    }
}
