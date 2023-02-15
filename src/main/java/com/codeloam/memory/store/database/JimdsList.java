package com.codeloam.memory.store.database;

/**
 * LinkedList.
 *
 * @author jinyu.li
 * @since 1.0
 */
public abstract class JimdsList extends JimdsData {
    @Override
    public DataType getDataType() {
        return DataType.List;
    }
}
