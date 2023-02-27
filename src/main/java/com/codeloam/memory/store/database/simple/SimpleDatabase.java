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
    private final JimdsHash<ByteWord, JimdsData> database;

    public SimpleDatabase() {
        database = new SimpleHash<>();
    }

    @Override
    public NetworkData execute(Command command) {
        try {
            switch (command.getDataType()) {
                case String:
                    return executeStringCommand(command);
                case System:
                    return executeSystemCommand(command);
                default:
                    return new NetworkError("Unsupported data type " + command.getName());
            }
        } catch (Throwable t) {
            return new NetworkError(t.getMessage());
        }
    }

    private NetworkData executeSystemCommand(Command command) {
        switch (command.getName()) {
            case "PING" -> {
                return new NetworkSimpleString(ByteWord.create("PONG"));
            }
            default -> throw new UnknownCommandException(command.getName());
        }
    }

    private NetworkData executeStringCommand(Command command) {
        switch (command.getName()) {
            case "GET" -> {
                JimdsData object = database.get(command.getKey());
                if (object == null) {
                    return NetworkBulkString.NULL;
                }
                if (object.getDataType() != DataType.String) {
                    return new NetworkError("Wrong value type");
                }
                return new NetworkBulkString(((JimdsString) object).get());
            }
            case "SET" -> {
                JimdsData object = database.get(command.getKey());
                if (object == null) {
                    object = new SimpleString(command.getValues().get(0));
                    database.set(command.getKey(), object);
                    return NetworkSimpleString.OK;
                } else {
                    if (object.getDataType() != DataType.String) {
                        return new NetworkError("Wrong value type");
                    }

                    ByteWord oldValue = ((JimdsString) object).get();
                    database.set(command.getKey(), new SimpleString(command.getValues().get(0)));
                    return new NetworkBulkString(oldValue);
                }
            }
            default -> throw new UnknownCommandException(command.getName());
        }
    }
}
