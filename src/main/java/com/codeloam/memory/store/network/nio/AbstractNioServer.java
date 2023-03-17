package com.codeloam.memory.store.network.nio;

import com.codeloam.memory.store.JimdsException;
import com.codeloam.memory.store.database.DatabaseType;
import com.codeloam.memory.store.measure.RequestMeasureCollector;
import com.codeloam.memory.store.network.AbstractServer;
import com.codeloam.memory.store.network.Client;
import com.codeloam.memory.store.network.ClientCommandReader;
import com.codeloam.memory.store.network.ClientRequestProcessor;
import com.codeloam.memory.store.network.CommandReader;
import com.codeloam.memory.store.network.RequestProcessor;
import com.codeloam.memory.store.network.data.ChannelDataWriter;
import com.codeloam.memory.store.network.data.NetworkData;
import com.codeloam.memory.store.network.data.NetworkError;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Abstract NIO server.
 *
 * @author jinyu.li
 * @since 1.0
 */
public abstract class AbstractNioServer extends AbstractServer {
    private final String host;
    private final int port;

    private ServerSocketChannel server;

    protected Selector selector;

    private final long selectTimeout;

    protected final CommandReader commandReader;

    private final AtomicBoolean stop;

    protected final RequestMeasureCollector requestMeasureCollector;

    /**
     * Init server with given host and port.
     *
     * @param host host
     * @param port port
     */
    public AbstractNioServer(String host, int port) {
        this(host, port, new ClientRequestProcessor(), DatabaseType.Simple, 1000);
    }

    /**
     * Init server with given database type, host and port.
     *
     * @param host host
     * @param port port
     * @param type database type
     */
    public AbstractNioServer(String host, int port, DatabaseType type) {
        this(host, port, new ClientRequestProcessor(), type, 1000);
    }

    /**
     * Init server with given host, port, and process.
     *
     * @param host             host
     * @param port             port
     * @param requestProcessor request processor
     * @param type             database type
     */
    public AbstractNioServer(String host, int port, RequestProcessor requestProcessor,
                             DatabaseType type, long selectTimeout) {
        super(type, requestProcessor);
        this.host = host;
        this.port = port;
        this.stop = new AtomicBoolean(false);
        this.commandReader = new ClientCommandReader();
        this.requestMeasureCollector = new RequestMeasureCollector("NIO");
        this.selectTimeout = selectTimeout;
    }

    @Override
    public void start() {
        try {
            startServer();

            while (!stop.get()) {
                preSleep();
                if (selector.select(selectTimeout) > 0) {
                    process(selector.selectedKeys().iterator());
                }
            }
            preShutDown();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Process a request.
     *
     * @param socketChannel socket
     */
    protected abstract void process(SocketChannel socketChannel);

    /**
     * Callback before server waits for connection.
     */
    protected void preSleep() {}

    /**
     * Callback before server is going to shut down.
     */
    protected void preShutDown() {}
    @Override
    public void close() throws Exception {
        stop.set(true);
        closeQuietly(selector);
        closeQuietly(server);
    }

    /**
     * Start server.
     *
     * @throws IOException if throw by socket
     */
    private void startServer() throws IOException {
        server = ServerSocketChannel.open();
        server.configureBlocking(false);

        SocketAddress address = new InetSocketAddress(host, port);
        server.socket().bind(address);

        selector = SelectorProvider.provider().openSelector();
        server.register(selector, SelectionKey.OP_ACCEPT);

        System.out.println("Jimds is ready to accept requests on port " + port);
    }

    /**
     * Write response data to socket.
     *
     * @param data          response
     * @param socketChannel socket to write to
     * @param close         whether close socket after write
     */
    protected void writeData(NetworkData data, SocketChannel socketChannel, boolean close) {
        try {
            data.write(new ChannelDataWriter(socketChannel));
        } catch (Throwable e) {
            // ignore the error
            e.printStackTrace();
        } finally {
            if (close) {
                closeQuietly(socketChannel);
            }
        }
    }

    /**
     * Register read operation for a socket.
     *
     * @param selectionKey key
     * @throws IOException if thrown by socket
     */
    protected void registerReadOperation(SelectionKey selectionKey) throws IOException {
        ServerSocketChannel nextReady = (ServerSocketChannel) selectionKey.channel();
        SocketChannel channel = nextReady.accept();
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_READ);
    }

    /**
     * Execute given command.
     *
     * @param client client
     */
    protected void executeCommand(Client client) {
        try {
            NetworkData data = requestProcessor.process(database, client.getCommand());
            client.getMeasureData().add(RequestMeasureCollector.FIELD_NAME_PROCESS);
            client.setResult(data);
        } catch (JimdsException e) {
            client.setClientError(true);
            client.getMeasureData().add(RequestMeasureCollector.FIELD_NAME_ERROR);
            // internal exception, write to output
            NetworkData data = new NetworkError(e.getMessage());
            client.setResult(data);
        } catch (Throwable e) {
            client.setClientError(true);
            client.getMeasureData().add(RequestMeasureCollector.FIELD_NAME_ERROR);
            // should only be thrown when writing data to output
            // if input throws IOException, an error message will be written to output.
            // throw new RuntimeException(e);
            // ignore the message, and close socket
            e.printStackTrace();
        }
    }

    /**
     * Process all ready sockets.
     *
     * @param iterator sockets iterator
     * @throws IOException if thrown by socket
     */
    protected void process(Iterator<SelectionKey> iterator) throws IOException {
        while (iterator.hasNext()) {
            SelectionKey selectionKey = iterator.next();
            if (selectionKey.isAcceptable()) {
                registerReadOperation(selectionKey);
            } else if (selectionKey.isReadable()) {
                SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                process(socketChannel);
            }
            iterator.remove();
        }
    }

}
