package com.codeloam.memory.store.command;

import com.codeloam.memory.store.database.DataType;
import com.codeloam.memory.store.network.ByteWord;
import com.codeloam.memory.store.util.ByteWordFactory;
import com.codeloam.memory.store.util.Pair;
import com.codeloam.memory.store.util.Triple;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.codeloam.memory.store.util.ByteWordFactory.getByteWord;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test CommandFactory.
 *
 * @author jinyu.li
 * @since 1.0
 */
public class CommandFactoryTest {
    @Test
    public void testParse() {
        List<Triple<CommandConfig, Command, String>> triples = List.of(
                new Triple<>(new CommandConfig("GET", true, false,
                        false, null, DataType.String),
                        new SimpleCommand("GET", DataType.String, getByteWord("test"), null, null),
                        "GET test"),
                new Triple<>(new CommandConfig("SET", true, true,
                        false, null, DataType.String),
                        new SimpleCommand("SET", DataType.String, getByteWord("test"), List.of(getByteWord("value")), null),
                        "SET test value"),
                new Triple<>(new CommandConfig("SET", true, true, false,
                        List.of(
                                new CommandOptionConfig("NX", false, false, false, null),
                                new CommandOptionConfig("EX", false, true, true, null),
                                new CommandOptionConfig("GET", false, false, false, null)),
                        DataType.String),
                        new SimpleCommand("SET", DataType.String, getByteWord("test"),
                                List.of(getByteWord("value")), Map.of("NX", ByteWord.NULL,
                                "GET", ByteWord.NULL, "EX", getByteWord("100"))),
                        "SET test value NX GET EX 100")
        );
        for (var triple : triples) {
            List<ByteWord> words = simpleParse(triple.value3());
            Command command = CommandFactory.getInstance().parse(triple.value1(), words);
            assertEquals(triple.value2(), command, triple.value3());
        }
    }

    @Test
    public void testParseFailed() {
        List<Pair<CommandConfig, String>> data = List.of(
                new Pair<>(new CommandConfig("GET", true, false,
                        false, null, DataType.String),
                        "GET"),
                new Pair<>(new CommandConfig("SET", true, true,
                        false, null, DataType.String),
                        "SET test "),
                new Pair<>(new CommandConfig("SET", true, true, false,
                        List.of(
                                new CommandOptionConfig("EX", false, true, false, null)),
                        DataType.String),
                        "SET test value EX"),
                new Pair<>(new CommandConfig("SET", true, true, false,
                        List.of(
                                new CommandOptionConfig("EX", true, true, false, null)),
                        DataType.String),
                        "SET test value"),
                new Pair<>(new CommandConfig("SET", true, true, false,
                        List.of(
                                new CommandOptionConfig("NX", false, false, false, null),
                                new CommandOptionConfig("EX", false, true, true, null),
                                new CommandOptionConfig("GET", false, false, false, null)),
                        DataType.String),
                        "SET test value NX GET EX 1a00")
        );
        for (var d : data) {
            List<ByteWord> words = simpleParse(d.value2());
            assertThrows(InvalidCommandException.class,
                    () -> CommandFactory.getInstance().parse(d.value1(), words),
                    d.value2());
        }
    }

    private List<ByteWord> simpleParse(String sentence) {
        return Arrays.stream(sentence.split(" ")).map(ByteWordFactory::getByteWord).collect(Collectors.toList());
    }
}
