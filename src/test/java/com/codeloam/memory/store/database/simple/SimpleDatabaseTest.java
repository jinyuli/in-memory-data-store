package com.codeloam.memory.store.database.simple;

import com.codeloam.memory.store.command.Command;
import com.codeloam.memory.store.command.SimpleCommand;
import com.codeloam.memory.store.database.DataType;
import com.codeloam.memory.store.database.Database;
import com.codeloam.memory.store.network.ByteWord;
import com.codeloam.memory.store.network.data.NetworkBulkString;
import com.codeloam.memory.store.network.data.NetworkData;
import com.codeloam.memory.store.network.data.NetworkSimpleString;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test SimpleDatabase.
 *
 * @author jinyu.li
 * @since 1.0
 */
public class SimpleDatabaseTest {
    @Test
    public void testExecuteWithStringCommand() {
        Database database = new SimpleDatabase();

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
}
