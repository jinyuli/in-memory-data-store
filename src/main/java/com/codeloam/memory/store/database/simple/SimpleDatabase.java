package com.codeloam.memory.store.database.simple;

import com.codeloam.memory.store.command.Command;
import com.codeloam.memory.store.command.InvalidCommandException;
import com.codeloam.memory.store.database.DataType;
import com.codeloam.memory.store.database.Database;
import com.codeloam.memory.store.database.JimdsData;
import com.codeloam.memory.store.database.JimdsHash;
import com.codeloam.memory.store.database.JimdsNumber;
import com.codeloam.memory.store.database.JimdsString;
import com.codeloam.memory.store.database.UnknownCommandException;
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
 * Simple database implementation.
 *
 * <p>This database is not thread safe.
 *
 * @author jinyu.li
 * @since 1.0
 */
public class SimpleDatabase implements Database {
    private static final ByteWord LONG_ONE = ByteWord.create(1L);
    private static final ByteWord DOUBLE_ONE = ByteWord.create(1.0D);
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
                case Number:
                    return executeNumberCommand(command);
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

    private NetworkData executeNumberCommand(Command command) {
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
                return number.isDecimal() ? new NetworkBulkString(object.get())
                        : new NetworkInteger(((SimpleLong) number).getValue());
            }
            case NUMBER_COMMAND_NSET -> {
                return setNumber(key, object, command.getValues().get(0), true, false, true);
            }
            case NUMBER_COMMAND_INCR -> {
                return setNumber(key, object, LONG_ONE, false, false, false);
            }
            case NUMBER_COMMAND_INCRBY -> {
                return setNumber(key, object, command.getValues().get(0), false, false, false);
            }
            case NUMBER_COMMAND_DECRBY -> {
                return setNumber(key, object, command.getValues().get(0), false, true, false);
            }
            case NUMBER_COMMAND_DECR -> {
                return setNumber(key, object, LONG_ONE, false, true, false);
            }
            default -> throw new UnknownCommandException(command.getName());
        }
    }

    private NetworkData setNumber(ByteWord key, JimdsData oldValue, ByteWord value,
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
                    : new NetworkBulkString(newObject.get());
        } else {
            if (oldValue.getDataType() != DataType.Number) {
                throw new InvalidCommandException("Wrong value type");
            }

            JimdsNumber number = (JimdsNumber) oldValue;
            ByteWord oldNumber = number.get();
            if (value.isDouble()) {
                number = isSetOperation ? number.setValue(value.getDouble())
                        : number.add(value.getDouble() * (negative ? -1 : 1));
            } else {
                number = isSetOperation ? number.setValue(value.getLong())
                        : number.add(value.getLong() * (negative ? -1 : 1));
            }
            database.set(key, number);

            ByteWord returnNumber = returnOldValue ? oldNumber : number.get();
            return number.isDecimal() ? new NetworkBulkString(returnNumber)
                    : new NetworkInteger(returnNumber.getLong());
        }
    }
}
