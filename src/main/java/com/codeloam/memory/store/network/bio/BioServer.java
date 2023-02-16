package com.codeloam.memory.store.network.bio;

import com.codeloam.memory.store.database.Database;
import com.codeloam.memory.store.database.DatabaseFactory;
import com.codeloam.memory.store.database.DatabaseType;
import com.codeloam.memory.store.network.AbstractServer;
import com.codeloam.memory.store.network.ClientCommandReader;
import com.codeloam.memory.store.network.ClientRequestProcessor;
import com.codeloam.memory.store.network.RequestProcessor;
import com.codeloam.memory.store.network.Server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * BIO server.
 *
 * @author jinyu.li
 * @since 1.0
 */
public class BioServer extends AbstractServer implements Server {
    private ServerSocket serverSocket;
    private final String host;
    private final int port;
    private final RequestProcessor requestProcessor;

    private final Database database;

    private final AtomicBoolean stop;

    /**
     * Init server with given host and port.
     *
     * @param host host
     * @param port port
     */
    public BioServer(String host, int port) {
        this(host, port, new ClientRequestProcessor(new ClientCommandReader()), DatabaseType.Simple);
    }

    /**
     * Init server with given database type, host and port.
     *
     * @param host host
     * @param port port
     * @param type database type
     */
    public BioServer(String host, int port, DatabaseType type) {
        this(host, port, new ClientRequestProcessor(new ClientCommandReader()), type);
    }

    /**
     * Init server with given host, port, and process.
     *
     * @param host             host
     * @param port             port
     * @param requestProcessor request processor
     * @param type             database type
     */
    public BioServer(String host, int port, RequestProcessor requestProcessor, DatabaseType type) {
        super(type);
        this.host = host;
        this.port = port;
        this.requestProcessor = requestProcessor;
        this.database = DatabaseFactory.create(type);
        this.stop = new AtomicBoolean(false);
    }

    /**
     * Start the server.
     * Process client request one by one.
     */
    public void start() {
        try {
            serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress(host, port));
            System.out.println("Jimds is ready to accept requests on port " + port);
            while (!stop.get()) {
                Socket socket = serverSocket.accept();
                process(socket);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            closeQuietly(serverSocket);
        }
    }

    @Override
    public void close() throws Exception {
        stop.set(true);
        closeQuietly(serverSocket);
    }

    private void process(Socket socket) {
        BufferedInputStream inputStream = null;
        BufferedOutputStream outputStream = null;
        try {
            inputStream = new BufferedInputStream(socket.getInputStream());
            outputStream = new BufferedOutputStream(socket.getOutputStream());
            requestProcessor.process(database, inputStream, outputStream);
            outputStream.flush();
        } catch (Throwable e) {
            // should only be thrown when writing data to output
            // if input throws IOException, an error message will be written to output.
            // throw new RuntimeException(e);
            // ignore the message, and close socket
            e.printStackTrace();
        } finally {
            closeQuietly(inputStream);
            closeQuietly(outputStream);
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
            e.printStackTrace();
        }
    }

}
