package com.codeloam.memory.store.database.simple;

import com.codeloam.memory.store.command.Command;
import com.codeloam.memory.store.database.DataType;
import com.codeloam.memory.store.database.Database;
import com.codeloam.memory.store.database.JimdsData;
import com.codeloam.memory.store.database.JimdsHash;
import com.codeloam.memory.store.database.JimdsObject;
import com.codeloam.memory.store.database.JimdsString;
import com.codeloam.memory.store.database.UnknownCommandException;
import com.codeloam.memory.store.network.ByteWord;
import com.codeloam.memory.store.network.data.NetworkBulkString;
import com.codeloam.memory.store.network.data.NetworkData;
import com.codeloam.memory.store.network.data.NetworkError;
import com.codeloam.memory.store.network.data.NetworkSimpleString;

/**
 * Simple database implementation.
 *
 * <p>This database is not thread safe.
 *
 * @author jinyu.li
 * @since 1.0
 */
public class SimpleDatabase implements Database {
    private final JimdsHash<ByteWord, JimdsObject> database;

    public SimpleDatabase() {
        database = new SimpleHash<>();
    }

    @Override
    public NetworkData execute(Command command) {
        try {
            switch (command.getDataType()) {
                case String:
                    return executeStringCommand(command);
                default:
                    return new NetworkError("Unsupported data type " + command.getName());
            }
        } catch (Throwable t) {
            return new NetworkError(t.getMessage());
        }
    }

    private NetworkData executeStringCommand(Command command) {
        switch (command.getName()) {
            case "GET" -> {
                JimdsObject object = database.get(command.getKey());
                if (object == null) {
                    return NetworkBulkString.NULL;
                }
                JimdsData data = object.getValue();
                if (data.getDataType() != DataType.String) {
                    return new NetworkError("Wrong value type");
                }
                return new NetworkBulkString(((JimdsString) data).get());
            }
            case "SET" -> {
                JimdsObject object = database.get(command.getKey());
                if (object == null) {
                    object = new JimdsObject();
                    object.setValue(new SimpleString(command.getValues().get(0)));
                    database.set(command.getKey(), object);
                    return NetworkSimpleString.OK;
                } else {
                    JimdsData data = object.getValue();
                    if (data.getDataType() != DataType.String) {
                        return new NetworkError("Wrong value type");
                    }

                    ByteWord oldValue = ((JimdsString) data).get();
                    object.setValue(new SimpleString(command.getValues().get(0)));
                    return new NetworkBulkString(oldValue);
                }
            }
            default -> throw new UnknownCommandException(command.getName());
        }
    }
}
