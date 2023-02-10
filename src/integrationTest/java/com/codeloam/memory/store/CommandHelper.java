package com.codeloam.memory.store;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Helper class to generate commands.
 *
 * @author jinyu.li
 * @since 1.0
 */
public class CommandHelper {
    private static final String END = "\r\n";
    private static final byte[] TEST = ("Lorem markdownum partu paterno Achillem. Habent amne generosi "
            + "aderant ad pellemnec erat sustinet merces columque haec et, dixit minus nutrit accipiam "
            + "subibissubdidit. Temeraria servatum agros qui sed fulva facta. Primum ultima, dedit, suo "
            + "quisque linguae medentes fixo: tum petis.").getBytes(StandardCharsets.UTF_8);

    /**
     * Generate string command, for set command, the value size is specified.
     *
     * @param count number of commands pair
     * @param size  value's bytes
     * @return commands
     */
    public static List<List<byte[]>> generateStringCommands(int count, int size) {
        List<List<byte[]>> commands = new ArrayList<>();
        for (int i = 0; i < count; ++i) {
            String set = String.format("SET key_%d_%d", size, i);
            commands.add(formatCommand(set, getRandomBytes(size)));
            String get = String.format("GET key_%d_%d", size, i);
            commands.add(formatCommand(get, null));
        }
        return commands;
    }

    /**
     * Generate string command, for set command, the value size is specified.
     *
     * @param count number of commands pair
     * @param size  value's bytes
     * @return commands
     */
    public static List<List<byte[]>> generateSharedStringCommands(int count, int size) {
        List<List<byte[]>> commands = new ArrayList<>();
        byte[] bytes = getRandomBytes(size);
        for (int i = 0; i < count; ++i) {
            String set = String.format("SET key_%d_%d", size, i);
            commands.add(formatCommand(set, bytes));
            String get = String.format("GET key_%d_%d", size, i);
            commands.add(formatCommand(get, null));
        }
        return commands;
    }

    /**
     * Generate string command.
     *
     * @param count number of commands pair
     * @return commands
     */
    public static List<List<byte[]>> generateStringCommands(int count) {
        List<List<byte[]>> commands = new ArrayList<>();
        for (int i = 0; i < count; ++i) {
            String set = String.format("SET key_%d value_%d", i, i);
            commands.add(formatCommand(set, null));
            String get = String.format("GET key_%d", i);
            commands.add(formatCommand(get, null));
        }
        return commands;
    }

    private static List<byte[]> formatCommand(String command, byte[] more) {
        StringBuilder sb = new StringBuilder();
        if (command == null) {
            sb.append("");
            return List.of(sb.toString().getBytes(StandardCharsets.UTF_8));
        }

        int extraWord = more == null ? 0 : 1;
        String[] words = command.split(" ");
        int totalLen = words.length + extraWord;
        if (totalLen == 0) {
            sb.append("*0").append(END);
            return List.of(sb.toString().getBytes(StandardCharsets.UTF_8));
        }
        if (totalLen == 1) {
            if (extraWord > 0) {
                sb.append("$").append(more.length).append(END);
                return List.of(sb.toString().getBytes(StandardCharsets.UTF_8), more, END.getBytes(StandardCharsets.UTF_8));
            } else {
                String w = words[0];
                sb.append("$").append(w.length()).append(END).append(w).append(END);
                return List.of(sb.toString().getBytes(StandardCharsets.UTF_8));
            }
        }
        sb.append("*").append(totalLen).append(END);
        for (String w : words) {
            sb.append("$").append(w.length()).append(END).append(w).append(END);
        }
        List<byte[]> result = new ArrayList<>();
        if (extraWord > 0) {
            sb.append("$").append(more.length).append(END);
            result.add(sb.toString().getBytes(StandardCharsets.UTF_8));
            result.add(more);
            result.add(END.getBytes(StandardCharsets.UTF_8));
        } else {
            result.add(sb.toString().getBytes(StandardCharsets.UTF_8));
        }
        return result;
    }

    private static byte[] getRandomBytes(int byteCount) {
        Random random = new Random();
        byte[] bytes = new byte[byteCount];
        int offset = 0;
        while (offset < byteCount) {
            int size = random.nextInt(TEST.length / 2);
            if (offset + size > byteCount) {
                size = byteCount - offset;
            }
            System.arraycopy(TEST, size, bytes, offset, size);
            offset += size;
        }
        return bytes;
    }
}
