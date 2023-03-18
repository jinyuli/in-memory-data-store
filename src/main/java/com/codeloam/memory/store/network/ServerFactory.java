package com.codeloam.memory.store.network;

import com.codeloam.memory.store.network.bio.BioServer;
import com.codeloam.memory.store.network.nio.VirtualThreadNioServer;
import com.codeloam.memory.store.network.nio.MultiThreadNioServer;
import com.codeloam.memory.store.network.nio.NioServer;

/**
 * Factory to create a server.
 *
 * @author jinyu.li
 * @since 1.0
 */
public class ServerFactory {
    /**
     * Create a server.
     *
     * @param host host
     * @param port port
     * @param writeAsync whether to write data back async, only applied when useNio is true
     * @param multiThread whether to use multiple thread, implies useNio, has higher priority than virtualThread
     * @param virtualThread whether to use virtual thread, implies useNio
     * @param useNio whether to use nio
     * @return a server instance
     */
    public static Server create(String host, int port, boolean writeAsync,
                                boolean multiThread, boolean virtualThread, boolean useNio) {
        if (multiThread) {
            System.out.println("Use multi-thread nio" + (writeAsync ? " with async write" : ""));
            return new MultiThreadNioServer(host, port, writeAsync);
        } else if (virtualThread) {
            System.out.println("Use virtual thread nio" + (writeAsync ? " with async write" : ""));
            return new VirtualThreadNioServer(host, port, writeAsync);
        } else if (useNio) {
            System.out.println("Use nio");
            return new NioServer(host, port);
        } else {
            System.out.println("Use bio");
            return new BioServer(host, port);
        }
    }
}
