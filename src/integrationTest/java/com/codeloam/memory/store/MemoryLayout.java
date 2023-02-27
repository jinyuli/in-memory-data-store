package com.codeloam.memory.store;

import com.codeloam.memory.store.database.JimdsObject;
import com.codeloam.memory.store.database.simple.SimpleString;
import com.codeloam.memory.store.network.ByteWord;
import org.openjdk.jol.info.ClassLayout;
import org.openjdk.jol.vm.VM;

import java.util.List;

/**
 * Show memory layout.
 *
 * @author jinyu.li
 * @since 1.0
 */
public class MemoryLayout {
    public static void main(String[] args) {
        System.out.println(VM.current().details());
        System.out.println(ClassLayout.parseClass(JimdsObject.class).toPrintable());
        System.out.println(ClassLayout.parseClass(SimpleString.class).toPrintable());
        System.out.println(ClassLayout.parseInstance(ByteWord.create(1L)).toPrintable());
        System.out.println(ClassLayout.parseInstance(ByteWord.create(new byte[]{'a', 'b', 'c'})).toPrintable());
        System.out.println(ClassLayout.parseInstance(ByteWord.create(List.of(new byte[]{'a','b','c'}, new byte[]{'a','b','c'}, new byte[]{'a','b','c'}))).toPrintable());
    }
}
