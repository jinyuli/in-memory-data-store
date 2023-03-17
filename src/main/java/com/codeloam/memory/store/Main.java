package com.codeloam.memory.store;

import com.codeloam.memory.store.network.Server;
import com.codeloam.memory.store.network.bio.BioServer;
import com.codeloam.memory.store.network.nio.FiberNioServer;
import com.codeloam.memory.store.network.nio.MultiThreadNioServer;
import com.codeloam.memory.store.network.nio.NioServer;

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
        try (Server server = new FiberNioServer("localhost", 3128)) {
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}