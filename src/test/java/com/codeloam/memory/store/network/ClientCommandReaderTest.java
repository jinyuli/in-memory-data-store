package com.codeloam.memory.store.network;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.codeloam.memory.store.util.ByteWordFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test class ClientCommandReader.
 */
public class ClientCommandReaderTest {
    /**
     * Test method read().
     */
    @Test
    public void testRead() {
        String longValue = "\"Lorem markdownum, a quoque nutu est *cumquat mandasset* veluti. "
                + "Passim inportuna totidemque nympha fert; repetens pendent, "
                + "poenarum guttura sed vacet\"";
        String[] commands = new String[]{
            "GET",
            "\"GET\"",
            "\"GET",
            "'GET'",
            "'GET",
            " GET KEY VALUE",
            longValue,
            "",
        };
        List<List<ByteWord>> expectedResults = List.of(
                List.of(getByteWord("GET")),
                List.of(getByteWord("\"GET\"")),
                List.of(getByteWord("\"GET")),
                List.of(getByteWord("'GET'")),
                List.of(getByteWord("'GET")),
                List.of(getByteWord("GET"), getByteWord("KEY"), getByteWord("VALUE")),
                List.of(getMultiByteWord(longValue)),
                List.of()
        );
        for (int i = 0; i < commands.length; ++i) {
            String command = commands[i];
            byte[] bytes = command.getBytes(StandardCharsets.UTF_8);
            InputStream inputStream = new ByteArrayInputStream(bytes);
            ClientCommandReader reader = new ClientCommandReader();
            try {
                List<ByteWord> words = reader.read(inputStream);
                List<ByteWord> expectedWords = expectedResults.get(i);
                assertEquals(expectedWords, words, command);
            } catch (Exception e) {
                Assertions.fail("failed with command:" + command, e);
            }
        }
    }

    private ByteWord getByteWord(String word) {
        return ByteWordFactory.getByteWord(word);
    }

    private ByteWord getMultiByteWord(String word) {
        return ByteWordFactory.getMultiByteWord(word);
    }
}
