package com.codeloam.memory.store.network.nio;

import com.codeloam.memory.store.JimdsException;
import com.codeloam.memory.store.command.Command;
import com.codeloam.memory.store.command.CommandFactory;
import com.codeloam.memory.store.database.DatabaseType;
import com.codeloam.memory.store.measure.MeasureData;
import com.codeloam.memory.store.measure.RequestMeasureCollector;
import com.codeloam.memory.store.network.ByteWord;
import com.codeloam.memory.store.network.Client;
import com.codeloam.memory.store.network.ClientRequestProcessor;
import com.codeloam.memory.store.network.RequestProcessor;
import com.codeloam.memory.store.network.data.ChannelDataReader;
import com.codeloam.memory.store.network.data.DataReader;
import com.codeloam.memory.store.network.data.NetworkData;
import com.codeloam.memory.store.network.data.NetworkError;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Nio server, requests are processed in Virtual Thread.
 *
 * @author jinyu.li
 * @since 1.0
 */
public class VirtualThreadNioServer extends AbstractNioServer {
    private final Queue<Client> queue;
    private final boolean writeAsync;

    public VirtualThreadNioServer(String host, int port) {
        this(host, port, DatabaseType.Simple);
    }

    public VirtualThreadNioServer(String host, int port, boolean writeAsync) {
        this(host, port, new ClientRequestProcessor(), DatabaseType.Simple, false);
    }

    public VirtualThreadNioServer(String host, int port, DatabaseType type) {
        this(host, port, new ClientRequestProcessor(), type, false);
    }

    /**
     * Constructor.
     *
     * @param host             host
     * @param port             port
     * @param requestProcessor request processor
     * @param type             database type
     * @param writeAsync       whether to write response async
     */
    public VirtualThreadNioServer(String host, int port, RequestProcessor requestProcessor,
                                  DatabaseType type, boolean writeAsync) {
        super(host, port, requestProcessor, type, 10);
        this.writeAsync = writeAsync;
        queue = new ConcurrentLinkedQueue<>();
    }

    @Override
    protected void process(Iterator<SelectionKey> iterator) throws IOException {
        while (iterator.hasNext()) {
            SelectionKey selectionKey = iterator.next();
            if (selectionKey.isAcceptable()) {
                registerReadOperation(selectionKey);
            } else if (selectionKey.isReadable()) {
                SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                selectionKey.cancel();
                process(socketChannel);
            }
            iterator.remove();
        }
        handleClients(false);
    }

    @Override
    protected void process(SocketChannel socketChannel) {
        Thread.ofVirtual().start(() -> {
            Client client = new Client(socketChannel);
            MeasureData measureData = new MeasureData();
            client.setMeasureData(measureData);
            try {
                measureData.add(RequestMeasureCollector.FIELD_NAME_CONNECTED);
                DataReader dataReader = new ChannelDataReader(socketChannel, bufSize, MAX_COMMAND_LENGTH);
                List<ByteWord> words = commandReader.read(dataReader);
                measureData.add(RequestMeasureCollector.FIELD_NAME_READ);
                Command command = CommandFactory.parseCommand(words);
                measureData.add(RequestMeasureCollector.FIELD_NAME_PARSE);
                client.setCommand(command);
            } catch (JimdsException e) {
                client.setClientError(true);
                measureData.add(RequestMeasureCollector.FIELD_NAME_ERROR);
                // internal exception, write to output
                NetworkData data = new NetworkError(e.getMessage());
                client.setResult(data);
            } catch (Throwable e) {
                client.setClientError(true);
                measureData.add(RequestMeasureCollector.FIELD_NAME_ERROR);
                // should only be thrown when writing data to output
                // if input throws IOException, an error message will be written to output.
                // throw new RuntimeException(e);
                // ignore the message, and close socket
                e.printStackTrace();
            }
            queue.offer(client);
        });
    }

    /**
     * Handle all clients in queue.
     *
     * @param forceSync whether to handle clients synchronized
     */
    private void handleClients(boolean forceSync) {
        List<Client> clients = new ArrayList<>();
        // only process clients in queue currently.
        while (!queue.isEmpty()) {
            clients.add(queue.poll());
        }
        for (Client client : clients) {
            if (client.isClientError()) {
                SocketChannel socketChannel = client.getSocketChannel();
                if (socketChannel.isOpen()) {
                    NetworkData data = client.getResult();
                    if (data == null) {
                        data = new NetworkError("Error");
                    }
                    writeData(data, socketChannel, true);
                }
            } else {
                executeCommand(client);
                if (!forceSync && writeAsync) {
                    Thread.ofVirtual().start(() -> {
                        writeData(client.getResult(), client.getSocketChannel(), true);
                    });
                } else {
                    writeData(client.getResult(), client.getSocketChannel(), true);
                }
            }
        }
    }

    @Override
    protected void preSleep() {
        super.preSleep();
        handleClients(false);
    }

    @Override
    protected void preShutDown() {
        super.preShutDown();
        // handle remaining clients
        handleClients(true);
    }
}
