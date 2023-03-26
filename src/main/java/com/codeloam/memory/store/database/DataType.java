package com.codeloam.memory.store.database;

/**
 * Represent data types.
 *
 * @author jinyu.li
 * @since 1.0
 */
public enum DataType {
    System(0),
    String(1),
    List(2),
    Hash(3),
    Set(4),
    SortedSet(5),
    Number(6);

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
