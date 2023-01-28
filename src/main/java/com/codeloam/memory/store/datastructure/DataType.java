package com.codeloam.memory.store.datastructure;

/**
 * Represent data types.
 *
 * @author jinyu.li
 * @since 1.0
 */
public enum DataType {
    String(0x01),
    List(0x01 << 1),
    Hash(0x01 << 2),
    Set(0x01 << 3),
    SortedSet(0x01 << 4);

    private final int type;

    DataType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    /**
     * Get an instance of DataType according to given type, or null.
     *
     * @param type type
     * @return DataType or null
     */
    public static DataType of(int type) {
        for (DataType data : values()) {
            if (data.type == type) {
                return data;
            }
        }
        return null;
    }
}
