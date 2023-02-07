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
    public static List<String> generateStringCommands(int count, int size) {
        List<String> commands = new ArrayList<>();
        for (int i = 0; i < count; ++i) {
            String set = String.format("SET key_%d_%d", size, i);
            commands.add(formatCommand(set, getRandomBytes(size)));
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
    public static List<String> generateStringCommands(int count) {
        List<String> commands = new ArrayList<>();
        for (int i = 0; i < count; ++i) {
            String set = String.format("SET key_%d value_%d", i, i);
            commands.add(formatCommand(set, null));
            String get = String.format("GET key_%d", i);
            commands.add(formatCommand(get, null));
        }
        return commands;
    }

    private static String formatCommand(String command, byte[] more) {
        StringBuilder sb = new StringBuilder();
        if (command == null) {
            sb.append("");
            return sb.toString();
        }

        int extraWord = more == null ? 0 : 1;
        String[] words = command.split(" ");
        int totalLen = words.length + extraWord;
        if (totalLen == 0) {
            sb.append("*0").append(END);
            return sb.toString();
        }
        if (totalLen == 1) {
            if (extraWord > 0) {
                sb.append("$").append(more.length).append(END).append(new String(more)).append(END);
                return sb.toString();
            } else {
                String w = words[0];
                sb.append("$").append(w.length()).append(END).append(w).append(END);
                return sb.toString();
            }
        }
        sb.append("*").append(totalLen).append(END);
        for (String w : words) {
            sb.append("$").append(w.length()).append(END).append(w).append(END);
        }
        if (extraWord > 0) {
            sb.append("$").append(more.length).append(END).append(new String(more)).append(END);
        }
        return sb.toString();
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
