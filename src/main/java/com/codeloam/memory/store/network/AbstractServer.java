package com.codeloam.memory.store.network;

import com.codeloam.memory.store.database.Database;
import com.codeloam.memory.store.database.DatabaseFactory;
import com.codeloam.memory.store.database.DatabaseType;

/**
 * @author jinyu.li
 * @since 1.0
 */
public abstract class AbstractServer implements Server{
    protected Database database;

    public AbstractServer(DatabaseType type) {
        database = DatabaseFactory.create(type);
    }
}
