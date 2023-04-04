package com.codeloam.memory.store.database.simple.executor;

import com.codeloam.memory.store.command.Command;
import com.codeloam.memory.store.command.InvalidCommandException;
import com.codeloam.memory.store.database.DataType;
import com.codeloam.memory.store.database.JimdsData;
import com.codeloam.memory.store.database.JimdsHash;
import com.codeloam.memory.store.database.JimdsNumber;
import com.codeloam.memory.store.database.UnknownCommandException;
import com.codeloam.memory.store.database.simple.SimpleLong;
import com.codeloam.memory.store.network.ByteWord;
import com.codeloam.memory.store.network.data.NetworkBulkString;
import com.codeloam.memory.store.network.data.NetworkData;
import com.codeloam.memory.store.network.data.NetworkError;
import com.codeloam.memory.store.network.data.NetworkInteger;
import com.codeloam.memory.store.network.data.NetworkSimpleString;

import static com.codeloam.memory.store.command.CommandFactory.NUMBER_COMMAND_DECR;
import static com.codeloam.memory.store.command.CommandFactory.NUMBER_COMMAND_DECRBY;
import static com.codeloam.memory.store.command.CommandFactory.NUMBER_COMMAND_INCR;
import static com.codeloam.memory.store.command.CommandFactory.NUMBER_COMMAND_INCRBY;
import static com.codeloam.memory.store.command.CommandFactory.NUMBER_COMMAND_NGET;
import static com.codeloam.memory.store.command.CommandFactory.NUMBER_COMMAND_NSET;

/**
 * Executor for number type.
 *
 * @author jinyu.li
 * @since 1.0
 */
public class NumberExecutor extends AbstractExecutor {
    private static final ByteWord LONG_ONE = ByteWord.create(1L);

    @Override
    public NetworkData execute(JimdsHash<ByteWord, JimdsData> database, Command command) {
        ByteWord key = command.getKey();
        JimdsData object = database.get(key);
        switch (command.getName()) {
            case NUMBER_COMMAND_NGET -> {
                if (object == null) {
                    return NetworkBulkString.NULL;
                }
                if (object.getDataType() != DataType.Number) {
                    return new NetworkError("Wrong value type");
                }
                JimdsNumber number = (JimdsNumber) object;
                return number.isDecimal() ? new NetworkBulkString(object.getData())
                        : new NetworkInteger(((SimpleLong) number).getValue());
            }
            case NUMBER_COMMAND_NSET -> {
                return setNumber(database, key, object, command.getValues().get(0), true, false, true);
            }
            case NUMBER_COMMAND_INCR -> {
                return setNumber(database, key, object, LONG_ONE, false, false, false);
            }
            case NUMBER_COMMAND_INCRBY -> {
                return setNumber(database, key, object, command.getValues().get(0), false, false, false);
            }
            case NUMBER_COMMAND_DECRBY -> {
                return setNumber(database, key, object, command.getValues().get(0), false, true, false);
            }
            case NUMBER_COMMAND_DECR -> {
                return setNumber(database, key, object, LONG_ONE, false, true, false);
            }
            default -> throw new UnknownCommandException(command.getName());
        }
    }

    private NetworkData setNumber(JimdsHash<ByteWord, JimdsData> database, ByteWord key, JimdsData oldValue, ByteWord value,
                                  boolean isSetOperation, boolean negative, boolean returnOldValue) {
        if (!value.isNumber()) {
            throw new InvalidCommandException("The command only supports number value");
        }

        if (oldValue == null) {
            JimdsNumber newObject = new SimpleLong(0L);
            if (value.isLong()) {
                newObject = newObject.add(value.getLong() * (negative ? -1 : 1));
            } else {
                newObject = newObject.add(value.getDouble() * (negative ? -1 : 1));
            }
            database.set(key, newObject);
            if (returnOldValue) {
                return NetworkSimpleString.OK;
            }
            return value.isLong() ? new NetworkInteger(((SimpleLong) newObject).getValue())
                    : new NetworkBulkString(newObject.getData());
        } else {
            if (oldValue.getDataType() != DataType.Number) {
                throw new InvalidCommandException("Wrong value type");
            }

            JimdsNumber number = (JimdsNumber) oldValue;
            ByteWord oldNumber = number.getData();
            if (value.isDouble()) {
                number = isSetOperation ? number.setValue(value.getDouble())
                        : number.add(value.getDouble() * (negative ? -1 : 1));
            } else {
                number = isSetOperation ? number.setValue(value.getLong())
                        : number.add(value.getLong() * (negative ? -1 : 1));
            }
            database.set(key, number);

            ByteWord returnNumber = returnOldValue ? oldNumber : number.getData();
            return number.isDecimal() ? new NetworkBulkString(returnNumber)
                    : new NetworkInteger(returnNumber.getLong());
        }
    }
}
