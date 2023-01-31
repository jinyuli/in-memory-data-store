package com.codeloam.memory.store.database;

/**
 * @author jinyu.li
 * @since 1.0
 */
public class JimdsObject {
    private JimdsData value;

    public JimdsData getValue() {
        return value;
    }

    public void setValue(JimdsData value) {
        if (value == null) {
            throw new NullPointerException("value cannot be null");
        }
        this.value = value;
    }
}
