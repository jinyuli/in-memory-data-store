package com.codeloam.memory.store.database.result;

/**
 * The type of data returned to client.
 *
 * @author jinyu.li
 * @since 1.0
 */
public enum ResultType {
    None(0),
    SimpleString(1),
    Error(2),
    Integer(3),
    BulkString(4),
    Array(5);

    private final int type;

    ResultType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    /**
     * Get an instance of ResultType according to given type, or null.
     *
     * @param type type
     * @return DataType or null
     */
    public static ResultType of(int type) {
        for (ResultType data : values()) {
            if (data.type == type) {
                return data;
            }
        }
        return null;
    }
}
