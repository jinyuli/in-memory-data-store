package com.codeloam.memory.store.database.simple.executor;

import com.codeloam.memory.store.command.Command;
import com.codeloam.memory.store.database.DataType;
import com.codeloam.memory.store.database.JimdsData;
import com.codeloam.memory.store.database.JimdsHash;
import com.codeloam.memory.store.database.UnknownCommandException;
import com.codeloam.memory.store.database.simple.SimpleString;
import com.codeloam.memory.store.network.ByteWord;
import com.codeloam.memory.store.network.data.NetworkBulkString;
import com.codeloam.memory.store.network.data.NetworkData;
import com.codeloam.memory.store.network.data.NetworkError;
import com.codeloam.memory.store.network.data.NetworkSimpleString;

import static com.codeloam.memory.store.command.CommandFactory.STRING_COMMAND_GET;
import static com.codeloam.memory.store.command.CommandFactory.STRING_COMMAND_SET;

/**
 * Executor for string type.
 *
 * @author jinyu.li
 * @since 1.0
 */
public class StringExecutor extends AbstractExecutor {
    @Override
    public NetworkData execute(JimdsHash<ByteWord, JimdsData> database, Command command) {
        switch (command.getName()) {
            case STRING_COMMAND_GET -> {
                JimdsData object = database.get(command.getKey());
                if (object == null) {
                    return NetworkBulkString.NULL;
                }
                if (object.getDataType() != DataType.String) {
                    return new NetworkError("Wrong value type");
                }
                return new NetworkBulkString(object.getData());
            }
            case STRING_COMMAND_SET -> {
                JimdsData object = database.get(command.getKey());
                if (object == null) {
                    object = new SimpleString(command.getValues().get(0));
                    database.set(command.getKey(), object);
                    return NetworkSimpleString.OK;
                } else {
                    if (object.getDataType() != DataType.String) {
                        return new NetworkError("Wrong value type");
                    }

                    ByteWord oldValue = object.getData();
                    database.set(command.getKey(), new SimpleString(command.getValues().get(0)));
                    return new NetworkBulkString(oldValue);
                }
            }
            default -> throw new UnknownCommandException(command.getName());
        }
    }
}
