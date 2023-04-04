package com.codeloam.memory.store.database.simple;

import com.codeloam.memory.store.command.Command;
import com.codeloam.memory.store.database.DataType;
import com.codeloam.memory.store.database.Database;
import com.codeloam.memory.store.database.JimdsData;
import com.codeloam.memory.store.database.JimdsHash;
import com.codeloam.memory.store.database.simple.executor.Executor;
import com.codeloam.memory.store.database.simple.executor.ListExecutor;
import com.codeloam.memory.store.database.simple.executor.NumberExecutor;
import com.codeloam.memory.store.database.simple.executor.StringExecutor;
import com.codeloam.memory.store.network.ByteWord;
import com.codeloam.memory.store.network.data.NetworkData;
import com.codeloam.memory.store.network.data.NetworkError;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple database implementation.
 *
 * <p>This database is not thread safe.
 *
 * @author jinyu.li
 * @since 1.0
 */
public class SimpleDatabase implements Database {
    private final JimdsHash<ByteWord, JimdsData> database;
    private final Map<DataType, Executor> executorMap;

    public SimpleDatabase() {
        database = new SimpleHash<>();
        executorMap = new HashMap<>();
        executorMap.put(DataType.String, new StringExecutor());
        executorMap.put(DataType.Number, new NumberExecutor());
        executorMap.put(DataType.List, new ListExecutor());
    }

    @Override
    public NetworkData execute(Command command) {
        try {
            Executor executor = executorMap.get(command.getDataType());
            if (executor == null) {
                return new NetworkError("Unsupported data type " + command.getName());
            }
            return executor.execute(database, command);
        } catch (Throwable t) {
            return new NetworkError(t.getMessage());
        }
    }
}
