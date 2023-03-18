package com.codeloam.memory.store.network.bio;

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
import com.codeloam.memory.store.network.data.DataReader;
import com.codeloam.memory.store.network.RequestProcessor;
import com.codeloam.memory.store.network.Server;
import com.codeloam.memory.store.network.data.StreamDataReader;
import com.codeloam.memory.store.network.data.StreamDataWriter;
import com.codeloam.memory.store.network.data.NetworkData;
import com.codeloam.memory.store.network.data.NetworkError;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
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

    private final CommandReader commandReader;
    private final AtomicBoolean stop;

    private final RequestMeasureCollector requestMeasureCollector;

    /**
     * Init server with given host and port.
     *
     * @param host host
     * @param port port
     */
    public BioServer(String host, int port) {
        this(host, port, new ClientRequestProcessor(), DatabaseType.Simple);
    }

    /**
     * Init server with given database type, host and port.
     *
     * @param host host
     * @param port port
     * @param type database type
     */
    public BioServer(String host, int port, DatabaseType type) {
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
    public BioServer(String host, int port, RequestProcessor requestProcessor, DatabaseType type) {
        super(type, requestProcessor);
        this.host = host;
        this.port = port;
        this.stop = new AtomicBoolean(false);
        this.commandReader = new ClientCommandReader();
        this.requestMeasureCollector = new RequestMeasureCollector("BIO");
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

    protected void process(Socket socket) {
        BufferedInputStream inputStream = null;
        BufferedOutputStream outputStream = null;
        MeasureData measureData = new MeasureData();
        try {
            measureData.add(RequestMeasureCollector.FIELD_NAME_CONNECTED);
            inputStream = new BufferedInputStream(socket.getInputStream());
            outputStream = new BufferedOutputStream(socket.getOutputStream());
            DataReader dataReader = new StreamDataReader(inputStream, bufSize, MAX_COMMAND_LENGTH);
            List<ByteWord> words = commandReader.read(dataReader);
            measureData.add(RequestMeasureCollector.FIELD_NAME_READ);
            Command command = CommandFactory.parseCommand(words);
            measureData.add(RequestMeasureCollector.FIELD_NAME_PARSE);
            NetworkData data = requestProcessor.process(database, command);
            measureData.add(RequestMeasureCollector.FIELD_NAME_PROCESS);
            data.write(new StreamDataWriter(outputStream));
            outputStream.flush();
            measureData.add(RequestMeasureCollector.FIELD_NAME_SEND);
        } catch (JimdsException e) {
            measureData.add(RequestMeasureCollector.FIELD_NAME_ERROR);
            // internal exception, write to output
            NetworkData data = new NetworkError(e.getMessage());
            try {
                data.write(new StreamDataWriter(outputStream));
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
            closeQuietly(inputStream);
            closeQuietly(outputStream);
            closeQuietly(socket);
        }
    }

}
