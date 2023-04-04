package com.codeloam.memory.store.database.simple.executor;

import com.codeloam.memory.store.command.Command;
import com.codeloam.memory.store.database.JimdsData;
import com.codeloam.memory.store.database.JimdsHash;
import com.codeloam.memory.store.database.JimdsList;
import com.codeloam.memory.store.database.UnknownCommandException;
import com.codeloam.memory.store.database.simple.SimpleList;
import com.codeloam.memory.store.network.ByteWord;
import com.codeloam.memory.store.network.data.NetworkArray;
import com.codeloam.memory.store.network.data.NetworkBulkString;
import com.codeloam.memory.store.network.data.NetworkData;
import com.codeloam.memory.store.network.data.NetworkError;
import com.codeloam.memory.store.network.data.NetworkInteger;
import com.codeloam.memory.store.network.data.NetworkSimpleString;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

import static com.codeloam.memory.store.command.CommandFactory.LIST_COMMAND_LINDEX;
import static com.codeloam.memory.store.command.CommandFactory.LIST_COMMAND_LLEN;
import static com.codeloam.memory.store.command.CommandFactory.LIST_COMMAND_LPOP;
import static com.codeloam.memory.store.command.CommandFactory.LIST_COMMAND_LPUSH;
import static com.codeloam.memory.store.command.CommandFactory.LIST_COMMAND_LREM;
import static com.codeloam.memory.store.command.CommandFactory.LIST_COMMAND_LSET;
import static com.codeloam.memory.store.command.CommandFactory.LIST_COMMAND_RPOP;
import static com.codeloam.memory.store.command.CommandFactory.LIST_COMMAND_RPUSH;
import static com.codeloam.memory.store.database.DataType.List;

/**
 * Executor for list type.
 *
 * @author jinyu.li
 * @since 1.0
 */
public class ListExecutor extends AbstractExecutor {

    @Override
    public NetworkData execute(JimdsHash<ByteWord, JimdsData> database, Command command) {
        ByteWord key = command.getKey();
        JimdsData object = database.get(key);
        switch (command.getName()) {
            case LIST_COMMAND_LPOP -> {
                if (object == null) {
                    return NetworkBulkString.NULL;
                }
                if (object.getDataType() != List) {
                    return new NetworkError("Wrong value type");
                }
                JimdsList<ByteWord> list = (JimdsList<ByteWord>) object;
                long count = 1;
                if (command.getValues().size() > 0) {
                    ByteWord value = command.getValues().get(0);
                    if (!value.isLong()) {
                        return new NetworkError("The given count is not a number");
                    }
                    count = value.getLong();
                }
                if (list.size() == 0) {
                    return NetworkBulkString.NULL;
                }
                Iterator<ByteWord> iterator = list.iterator();
                java.util.List<NetworkData> result = new ArrayList<>();
                while (iterator.hasNext() && count > 0) {
                    result.add(new NetworkBulkString(iterator.next()));
                    iterator.remove();
                }
                if (count == 1) {
                    return result.get(0);
                }
                return new NetworkArray(result);
            }
            case LIST_COMMAND_RPOP -> {
                if (object == null) {
                    return NetworkBulkString.NULL;
                }
                if (object.getDataType() != List) {
                    return new NetworkError("Wrong value type");
                }
                JimdsList<ByteWord> list = (JimdsList<ByteWord>) object;
                long count = 1;
                if (command.getValues().size() > 0) {
                    ByteWord value = command.getValues().get(0);
                    if (!value.isLong()) {
                        return new NetworkError("The given count is not a number");
                    }
                    count = value.getLong();
                }
                if (list.size() == 0) {
                    return NetworkBulkString.NULL;
                }
                ListIterator<ByteWord> iterator = list.reverseIterator();
                java.util.List<NetworkData> result = new ArrayList<>();
                while (iterator.hasPrevious() && count > 0) {
                    result.add(new NetworkBulkString(iterator.previous()));
                    iterator.remove();
                }
                if (count == 1) {
                    return result.get(0);
                }
                return new NetworkArray(result);
            }
            case LIST_COMMAND_LPUSH -> {
                JimdsList<ByteWord> list = (JimdsList<ByteWord>) object;
                if (list == null) {
                    list = new SimpleList<>();
                    database.set(key, list);
                }
                if (list.getDataType() != List) {
                    return new NetworkError("Wrong value type");
                }
                for (ByteWord value : command.getValues()) {
                    list.addFirst(value);
                }
                return new NetworkInteger(list.size());
            }
            case LIST_COMMAND_RPUSH -> {
                JimdsList<ByteWord> list = (JimdsList<ByteWord>) object;
                if (list == null) {
                    list = new SimpleList<>();
                    database.set(key, list);
                }
                if (list.getDataType() != List) {
                    return new NetworkError("Wrong value type");
                }
                for (ByteWord value : command.getValues()) {
                    list.addLast(value);
                }
                return new NetworkInteger(list.size());
            }
            case LIST_COMMAND_LINDEX -> {
                JimdsList<ByteWord> list = (JimdsList<ByteWord>) object;
                if (list == null) {
                    return new NetworkInteger(0);
                }
                if (list.getDataType() != List) {
                    return new NetworkError("Wrong value type");
                }
                ByteWord value = command.getValues().get(0);
                if (!value.isNumber()) {
                    return new NetworkError("index should be a number");
                }
                if (list.size() == 0) {
                    return NetworkBulkString.NULL;
                }
                int index = (int) value.getLong();
                if (index < 0) {
                    index = index + list.size();
                }
                if (index < 0 || index >= list.size()) {
                    return NetworkBulkString.NULL;
                }

                return new NetworkBulkString(list.get(index));
            }
            case LIST_COMMAND_LSET -> {
                JimdsList<ByteWord> list = (JimdsList<ByteWord>) object;
                if (list == null) {
                    return new NetworkInteger(0);
                }
                if (list.getDataType() != List) {
                    return new NetworkError("Wrong value type");
                }
                if (command.getValues().size() < 2) {
                    return new NetworkError("Need element for LSET command");
                }
                ByteWord value = command.getValues().get(0);
                if (!value.isNumber()) {
                    return new NetworkError("index should be a number");
                }
                if (list.size() == 0) {
                    return new NetworkError("Index out of range");
                }
                int index = (int) value.getLong();
                if (index < 0) {
                    index = index + list.size();
                }
                if (index < 0 || index >= list.size()) {
                    return new NetworkError("Index out of range");
                }
                list.set(index, command.getValues().get(1));

                return NetworkSimpleString.OK;
            }
            case LIST_COMMAND_LREM -> {
                JimdsList<ByteWord> list = (JimdsList<ByteWord>) object;
                if (list == null) {
                    return new NetworkInteger(0);
                }
                if (list.getDataType() != List) {
                    return new NetworkError("Wrong value type");
                }
                if (list.size() == 0) {
                    return new NetworkInteger(0);
                }

                if (command.getValues().size() < 2) {
                    return new NetworkError("Need element for LREM command");
                }
                ByteWord value = command.getValues().get(0);
                if (!value.isNumber()) {
                    return new NetworkError("index should be a number");
                }

                ByteWord element = command.getValues().get(1);
                int count = (int) value.getLong();
                int number = 0;
                if (count < 0) {
                    count = count * -1;
                    ListIterator<ByteWord> iterator = list.reverseIterator();
                    while (iterator.hasPrevious() && number < count) {
                        ByteWord v = iterator.previous();
                        if (v.equals(element)) {
                            iterator.remove();
                            number++;
                        }
                    }
                } else {
                    if (count == 0) {
                        count = list.size();
                    }
                    Iterator<ByteWord> iterator = list.iterator();
                    while (iterator.hasNext() && number < count) {
                        ByteWord v = iterator.next();
                        if (v.equals(element)) {
                            iterator.remove();
                            number++;
                        }
                    }
                }

                return new NetworkInteger(number);
            }
            case LIST_COMMAND_LLEN -> {
                JimdsList<ByteWord> list = (JimdsList<ByteWord>) object;
                if (list == null) {
                    return new NetworkInteger(0);
                }
                return new NetworkInteger(list.size());
            }
            default -> throw new UnknownCommandException(command.getName());
        }
    }
}
