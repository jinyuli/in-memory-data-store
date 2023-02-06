package com.codeloam.memory.store;

import com.codeloam.memory.store.network.Server;
import com.codeloam.memory.store.network.bio.BioServer;

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
        try (Server server = new BioServer("localhost", 3128)) {
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}