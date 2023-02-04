package com.codeloam.memory.store.database;

/**
 * Object that is stored in Database.
 *
 * @author jinyu.li
 * @since 1.0
 */
public class JimdsObject {
    private JimdsData value;

    public JimdsData getValue() {
        return value;
    }

    /**
     * Set value.
     *
     * @param value value
     *
     * @throws NullPointerException if value is null
     */
    public void setValue(JimdsData value) {
        if (value == null) {
            throw new NullPointerException("value cannot be null");
        }
        this.value = value;
    }
}
