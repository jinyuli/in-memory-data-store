package com.codeloam.memory.store.database.simple;

import com.codeloam.memory.store.command.Command;
import com.codeloam.memory.store.command.CommandFactory;
import com.codeloam.memory.store.command.SimpleCommand;
import com.codeloam.memory.store.database.DataType;
import com.codeloam.memory.store.database.Database;
import com.codeloam.memory.store.network.ByteWord;
import com.codeloam.memory.store.network.data.NetworkBulkString;
import com.codeloam.memory.store.network.data.NetworkData;
import com.codeloam.memory.store.network.data.NetworkError;
import com.codeloam.memory.store.network.data.NetworkInteger;
import com.codeloam.memory.store.network.data.NetworkSimpleString;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test SimpleDatabase.
 *
 * @author jinyu.li
 * @since 1.0
 */
public class SimpleDatabaseTest {

    private SimpleDatabase database;

    @BeforeEach
    public void setup() {
        database = new SimpleDatabase();
    }

    @Test
    public void testExecuteWithStringCommand() {
        Command set = new SimpleCommand("SET", DataType.String, ByteWord.create("key"),
                List.of(ByteWord.create("value")), null);
        NetworkData result = database.execute(set);
        assertEquals(result, NetworkSimpleString.OK);

        set = new SimpleCommand("SET", DataType.String, ByteWord.create("key"),
                List.of(ByteWord.create("value2")), null);
        result = database.execute(set);
        assertEquals(result, new NetworkBulkString(ByteWord.create("value")));

        Command get = new SimpleCommand("GET", DataType.String, ByteWord.create("key"), null, null);
        result = database.execute(get);
        assertEquals(result, new NetworkBulkString(ByteWord.create("value2")));
    }

    @Test
    public void testExecuteWithNSetCommand() {
        ByteWord key = ByteWord.create("key");
        Command set = new SimpleCommand("NSET", DataType.Number,
                key, List.of(ByteWord.create(1L)), null);
        NetworkData result =database.execute(set);
        assertEquals(NetworkSimpleString.OK, result);

        set = new SimpleCommand("NSET", DataType.Number,
                key, List.of(ByteWord.create(2L)), null);
        result =database.execute(set);
        assertEquals(new NetworkInteger(1L), result);

        Command get = new SimpleCommand(CommandFactory.NUMBER_COMMAND_NGET, DataType.Number,
                key, null, null);
        result =database.execute(get);
        assertEquals(new NetworkInteger(2L), result);
    }

    @Test
    public void testExecuteWithNSetCommandWithException() {
        ByteWord key = ByteWord.create("key");
        Command set = new SimpleCommand("NSET", DataType.Number,
                key, List.of(ByteWord.create("test")), null);
        NetworkData result =database.execute(set);
        assertTrue(result instanceof NetworkError);
    }


    @Test
    public void testExecuteWithIncrCommand() {
        ByteWord key = ByteWord.create("key");
        Command incr = new SimpleCommand(CommandFactory.NUMBER_COMMAND_INCR, DataType.Number,
                key, null, null);
        NetworkData result =database.execute(incr);
        assertEquals(new NetworkInteger(1L), result);

        incr = new SimpleCommand(CommandFactory.NUMBER_COMMAND_INCR, DataType.Number,
                key, null, null);
        result =database.execute(incr);
        assertEquals(new NetworkInteger(2L), result);
    }

    @Test
    public void testExecuteWithIncrByCommandWithException() {
        ByteWord key = ByteWord.create("key");
        Command incr = new SimpleCommand(CommandFactory.NUMBER_COMMAND_INCRBY, DataType.Number,
                key, List.of(ByteWord.create("not a number")), null);
        NetworkData result =database.execute(incr);
        assertTrue(result instanceof NetworkError);
    }

    @Test
    public void testExecuteWithIncrByCommand() {
        ByteWord key = ByteWord.create("key");
        Command incr = new SimpleCommand(CommandFactory.NUMBER_COMMAND_INCRBY, DataType.Number,
                key, List.of(ByteWord.create(10L)), null);
        NetworkData result =database.execute(incr);
        assertEquals(new NetworkInteger(10L), result);

        incr = new SimpleCommand(CommandFactory.NUMBER_COMMAND_INCRBY, DataType.Number,
                key, List.of(ByteWord.create(5L)), null);
        result =database.execute(incr);
        assertEquals(new NetworkInteger(15L), result);
    }

    @Test
    public void testExecuteWithDecrCommand() {
        ByteWord key = ByteWord.create("key");
        Command incr = new SimpleCommand(CommandFactory.NUMBER_COMMAND_DECR, DataType.Number,
                key, null, null);
        NetworkData result =database.execute(incr);
        assertEquals(new NetworkInteger(-1L), result);

        incr = new SimpleCommand(CommandFactory.NUMBER_COMMAND_DECR, DataType.Number,
                key, null, null);
        result =database.execute(incr);
        assertEquals(new NetworkInteger(-2L), result);
    }

    @Test
    public void testExecuteWithDecrByCommandWithException() {
        ByteWord key = ByteWord.create("key");
        Command incr = new SimpleCommand(CommandFactory.NUMBER_COMMAND_DECRBY, DataType.Number,
                key, List.of(ByteWord.create("not a number")), null);
        NetworkData result =database.execute(incr);
        assertTrue(result instanceof NetworkError);
    }

    @Test
    public void testExecuteWithDecrByCommand() {
        ByteWord key = ByteWord.create("key");
        Command incr = new SimpleCommand(CommandFactory.NUMBER_COMMAND_DECRBY, DataType.Number,
                key, List.of(ByteWord.create(10L)), null);
        NetworkData result =database.execute(incr);
        assertEquals(new NetworkInteger(-10L), result);

        incr = new SimpleCommand(CommandFactory.NUMBER_COMMAND_DECRBY, DataType.Number,
                key, List.of(ByteWord.create(5L)), null);
        result =database.execute(incr);
        assertEquals(new NetworkInteger(-15L), result);
    }
}
