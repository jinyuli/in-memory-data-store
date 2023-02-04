package com.codeloam.memory.store.network;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import com.codeloam.memory.store.util.ByteWordFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test class ClientCommandReader.
 */
public class ClientCommandReaderTest {
    /**
     * Test method read() with RESP data.
     */
    @Test
    public void testReadResp() {
        String[] commands = new String[]{
            "+OK\r\n",
            "-ERROR\r\n",
            ":1000\r\n",
            "$5\r\nhello\r\n",
            "$0\r\n\r\n",
            "$-1\r\n",
            "*0\r\n",
            "*-1\r\n",
            "*4\r\n+OK\r\n-ERROR\r\n:1000\r\n$5\r\nhello\r\n",
        };
        List<List<ByteWord>> expectedResults = List.of(
                List.of(getByteWord("OK")),
                List.of(getByteWord("ERROR")),
                List.of(getByteWord(1000)),
                List.of(getByteWord("hello")),
                List.of(),
                Arrays.asList(new ByteWord[]{null}),
                List.of(),
                Arrays.asList(new ByteWord[]{null}),
                List.of(getByteWord("OK"), getByteWord("ERROR"), getByteWord(1000), getByteWord("hello"))
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

    /**
     * Test method read() with normal data separated with whitespace.
     */
    @Test
    public void testReadNormal() {
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
        };
        List<List<ByteWord>> expectedResults = List.of(
                List.of(getByteWord("GET")),
                List.of(getByteWord("\"GET\"")),
                List.of(getByteWord("\"GET")),
                List.of(getByteWord("'GET'")),
                List.of(getByteWord("'GET")),
                List.of(getByteWord("GET"), getByteWord("KEY"), getByteWord("VALUE")),
                List.of(getMultiByteWord(longValue))
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

    private ByteWord getByteWord(long word) {
        return ByteWordFactory.getByteWord(word);
    }

    private ByteWord getByteWord(String word) {
        return ByteWordFactory.getByteWord(word);
    }

    private ByteWord getMultiByteWord(String word) {
        return ByteWordFactory.getMultiByteWord(word);
    }
}
