package com.codeloam.memory.store;

import com.codeloam.memory.store.network.Server;
import com.codeloam.memory.store.network.ServerFactory;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Main.
 */
public class Main {
    /**
     * Main function, start server.
     *
     * @param args args
     */
    public static void main(String[] args) {
        Options options = new Options();
        options.addOption("h", "host", true, "listening host, default localhost");
        options.addOption("p", "port", true, "listening port, default 3128");
        options.addOption("nio", "use-nio", false, "use NIO");
        options.addOption("mt", "multi-thread", false, "use multiple thread to read data from socket");
        options.addOption("vt", "virtual-thread", false, "use virtual thread to read data from socket, only available since JDK 19");
        options.addOption("wa", "write-async", false, "use multiple thread to write data to socket");

        Server server = null;
        try {
            CommandLineParser parser = new DefaultParser();
            CommandLine commandLine = parser.parse(options, args);
            String host = commandLine.getOptionValue("host", "localhost");
            String portStr = commandLine.getOptionValue("port", "3128");
            int port = Integer.parseInt(portStr);
            if (port <= 0) {
                throw new IllegalArgumentException("Invalid port " + portStr);
            }
            boolean writeAsync = commandLine.hasOption("write-async");
            boolean useNio = commandLine.hasOption("use-nio");
            boolean multiThread = commandLine.hasOption("multi-thread");
            boolean virtualThread = commandLine.hasOption("virtual-thread");

            server = ServerFactory.create(host, port, writeAsync, multiThread, virtualThread, useNio);
            server.start();
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            if (server != null) {
                try {
                    server.close();
                } catch (Exception e) {
                    // igore the error
                }
            }
        }
    }
}