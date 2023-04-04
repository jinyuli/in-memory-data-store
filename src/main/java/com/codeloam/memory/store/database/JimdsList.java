package com.codeloam.memory.store.database;

import java.util.Iterator;
import java.util.ListIterator;

/**
 * LinkedList.
 *
 * @author jinyu.li
 * @since 1.0
 */
public abstract class JimdsList<T> extends JimdsData {
    @Override
    public DataType getDataType() {
        return DataType.List;
    }

    public abstract ListIterator<T> reverseIterator();
    public abstract ListIterator<T> iterator();
    public abstract int size();
    public abstract void addFirst(T t);
    public abstract void addLast(T t);
    public abstract T removeFirst();
    public abstract T removeLast();
    public abstract T set(int index, T t);
    public abstract T get(int index);
}
