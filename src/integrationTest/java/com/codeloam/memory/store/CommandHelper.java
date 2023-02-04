package com.codeloam.memory.store;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jinyu.li
 * @since 1.0
 */
public class CommandHelper {
    private static final String END = "\r\n";

    public static List<String> generateStringCommands(int count) {
        List<String> commands = new ArrayList<>();
        for (int i = 0; i < count; ++i) {
            String set = String.format("SET key_%d value_%d", i, i);
            commands.add(formatCommand(set));
            String get = String.format("GET key_%d", i);
            commands.add(formatCommand(get));
        }
        return commands;
    }

    private static String formatCommand(String command) {
        StringBuilder sb = new StringBuilder();
        if (command == null) {
            sb.append("");
            return sb.toString();
        }

        String[] words = command.split(" ");
        if (words.length == 0) {
            sb.append("*0").append(END);
            return sb.toString();
        }
        if (words.length == 1) {
            String w = words[0];
            sb.append("$").append(w.length()).append(END).append(w).append(END);
            return sb.toString();
        }
        sb.append("*").append(words.length).append(END);
        for (String w: words) {
            sb.append("$").append(w.length()).append(END).append(w).append(END);
        }
        return sb.toString();
    }
}
