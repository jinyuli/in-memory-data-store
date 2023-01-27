package com.codeloam.memory.store.network.bio;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import com.codeloam.memory.store.network.ClientRequestProcessor;
import com.codeloam.memory.store.network.RequestProcessor;
import com.codeloam.memory.store.network.Server;

/**
 * BIO server.
 *
 * @author jinyu.li
 * @since 1.0
 */
public class BioServer implements Server {
    private ServerSocket serverSocket;
    private final String host;
    private final int port;
    private final RequestProcessor requestProcessor;

    /**
     * Init server with given host and port.
     *
     * @param host host
     * @param port port
     */
    public BioServer(String host, int port) {
        this(host, port, new ClientRequestProcessor());
    }

    /**
     * Init server with given host, port, and process.
     *
     * @param host host
     * @param port port
     * @param requestProcessor request processor
     */
    public BioServer(String host, int port, RequestProcessor requestProcessor) {
        this.host = host;
        this.port = port;
        this.requestProcessor = requestProcessor;
    }

    /**
     * Start the server.
     * Process client request one by one.
     */
    public void start() {
        try {
            serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress(host, port));
            while (true) {
                Socket socket = serverSocket.accept();
                process(socket);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws Exception {
        closeQuietly(serverSocket);
    }

    private void process(Socket socket) {
        BufferedInputStream inputStream = null;
        try {
            inputStream = new BufferedInputStream(socket.getInputStream());
            requestProcessor.process(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            closeQuietly(inputStream);
            closeQuietly(socket);
        }
    }

    private void closeQuietly(Closeable c) {
        try {
            if (c != null) {
                c.close();
            }
        } catch (IOException e) {
            // ignore
        }
    }

}
