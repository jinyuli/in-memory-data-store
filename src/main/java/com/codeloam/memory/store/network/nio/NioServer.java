package com.codeloam.memory.store.network.nio;

import com.codeloam.memory.store.JimdsException;
import com.codeloam.memory.store.command.Command;
import com.codeloam.memory.store.command.CommandFactory;
import com.codeloam.memory.store.database.DatabaseType;
import com.codeloam.memory.store.measure.MeasureData;
import com.codeloam.memory.store.measure.RequestMeasureCollector;
import com.codeloam.memory.store.network.AbstractServer;
import com.codeloam.memory.store.network.ByteWord;
import com.codeloam.memory.store.network.ClientCommandReader;
import com.codeloam.memory.store.network.ClientRequestProcessor;
import com.codeloam.memory.store.network.CommandReader;
import com.codeloam.memory.store.network.RequestProcessor;
import com.codeloam.memory.store.network.Server;
import com.codeloam.memory.store.network.data.ChannelDataReader;
import com.codeloam.memory.store.network.data.ChannelDataWriter;
import com.codeloam.memory.store.network.data.DataReader;
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
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Server with java NIO.
 *
 * @author jinyu.li
 * @since 1.0
 */
public class NioServer extends AbstractServer implements Server {
    private final String host;
    private final int port;

    private ServerSocketChannel server;

    private Selector selector;

    private final CommandReader commandReader;

    private final AtomicBoolean stop;

    private final RequestMeasureCollector requestMeasureCollector;

    /**
     * Init server with given host and port.
     *
     * @param host host
     * @param port port
     */
    public NioServer(String host, int port) {
        this(host, port, new ClientRequestProcessor(), DatabaseType.Simple);
    }

    /**
     * Init server with given database type, host and port.
     *
     * @param host host
     * @param port port
     * @param type database type
     */
    public NioServer(String host, int port, DatabaseType type) {
        this(host, port, new ClientRequestProcessor(), type);
    }

    /**
     * Init server with given host, port, and process.
     *
     * @param host             host
     * @param port             port
     * @param requestProcessor request processor
     * @param type             database type
     */
    public NioServer(String host, int port, RequestProcessor requestProcessor, DatabaseType type) {
        super(type, requestProcessor);
        this.host = host;
        this.port = port;
        this.stop = new AtomicBoolean(false);
        this.commandReader = new ClientCommandReader();
        this.requestMeasureCollector = new RequestMeasureCollector("NIO");
    }

    @Override
    public void start() {
        try {
            server = ServerSocketChannel.open();
            server.configureBlocking(false);

            SocketAddress address = new InetSocketAddress(host, port);
            server.socket().bind(address);

            selector = SelectorProvider.provider().openSelector();
            server.register(selector, SelectionKey.OP_ACCEPT);

            System.out.println("Jimds is ready to accept requests on port " + port);

            int times = 0;
            while (!stop.get()) {
                while (selector.select(1000) > 0 && !stop.get()) {
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()) {
                        SelectionKey selectionKey = iterator.next();
                        if (selectionKey.isAcceptable()) {
                            ServerSocketChannel nextReady = (ServerSocketChannel) selectionKey.channel();
                            SocketChannel channel = nextReady.accept();
                            channel.configureBlocking(false);
                            channel.register(selector, SelectionKey.OP_READ);
                        } else if (selectionKey.isReadable()) {
                            SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                            process(socketChannel);
                        }
                        iterator.remove();
                    }
                }
                times++;
                if (times % 10 == 0) {
                    times = 0;
                    requestMeasureCollector.print();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws Exception {
        stop.set(true);
        closeQuietly(selector);
        closeQuietly(server);
    }

    protected void process(SocketChannel socketChannel) {
        MeasureData measureData = new MeasureData();
        try {
            measureData.add(RequestMeasureCollector.FIELD_NAME_CONNECTED);
            DataReader dataReader = new ChannelDataReader(socketChannel, bufSize, MAX_COMMAND_LENGTH);
            List<ByteWord> words = commandReader.read(dataReader);
            measureData.add(RequestMeasureCollector.FIELD_NAME_READ);
            Command command = CommandFactory.parseCommand(words);
            measureData.add(RequestMeasureCollector.FIELD_NAME_PARSE);
            NetworkData data = requestProcessor.process(database, command);
            measureData.add(RequestMeasureCollector.FIELD_NAME_PROCESS);
            data.write(new ChannelDataWriter(socketChannel));
            measureData.add(RequestMeasureCollector.FIELD_NAME_SEND);
        } catch (JimdsException e) {
            measureData.add(RequestMeasureCollector.FIELD_NAME_ERROR);
            // internal exception, write to output
            NetworkData data = new NetworkError(e.getMessage());
            try {
                data.write(new ChannelDataWriter(socketChannel));
            } catch (IOException ex) {
                e.printStackTrace();
            }
        } catch (Throwable e) {
            measureData.add(RequestMeasureCollector.FIELD_NAME_ERROR);
            // should only be thrown when writing data to output
            // if input throws IOException, an error message will be written to output.
            // throw new RuntimeException(e);
            // ignore the message, and close socket
            e.printStackTrace();
        } finally {
            requestMeasureCollector.add(measureData);
            closeQuietly(socketChannel);
        }
    }

}
