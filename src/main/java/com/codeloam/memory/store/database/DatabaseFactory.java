package com.codeloam.memory.store.database;

import com.codeloam.memory.store.database.simple.SimpleDatabase;

/**
 * Factory to create Database instance.
 *
 * @author jinyu.li
 * @since 1.0
 */
public class DatabaseFactory {
    /**
     * Create an instance of Database with given type.
     * Currently only support SimpleDatabase.
     *
     * @param type database type
     * @return an instance of Database
     */
    public static Database create(DatabaseType type) {
        return new SimpleDatabase();
    }
}
