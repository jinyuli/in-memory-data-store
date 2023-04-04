package com.codeloam.memory.store.database.simple;

import com.codeloam.memory.store.database.JimdsList;
import com.codeloam.memory.store.network.ByteWord;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * Simple implementation of JimdsList.
 *
 * @author jinyu.li
 * @since 1.0
 */
public class SimpleList<T> extends JimdsList<T> {
    private final List<T> list;

    public SimpleList() {
        list = new LinkedList<>();
    }

    @Override
    public ByteWord getData() {
        return null;
    }

    @Override
    public ListIterator<T> reverseIterator() {
        return list.listIterator(list.size() - 1);
    }

    @Override
    public ListIterator<T> iterator() {
        return list.listIterator();
    }

    public int size() {
        return list.size();
    }

    @Override
    public void addFirst(T t) {
        list.add(0, t);
    }

    @Override
    public void addLast(T t) {
        list.add(t);
    }

    @Override
    public T removeFirst() {
        return list.remove(0);
    }

    @Override
    public T removeLast() {
        return list.remove(list.size() - 1);
    }

    @Override
    public T set(int index, T t) {
        return list.set(index, t);
    }

    @Override
    public T get(int index) {
        return list.get(index);
    }
}
