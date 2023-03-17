package com.codeloam.memory.store.network.nio;

import com.codeloam.memory.store.JimdsException;
import com.codeloam.memory.store.command.Command;
import com.codeloam.memory.store.command.CommandFactory;
import com.codeloam.memory.store.database.DatabaseType;
import com.codeloam.memory.store.measure.MeasureData;
import com.codeloam.memory.store.measure.RequestMeasureCollector;
import com.codeloam.memory.store.network.ByteWord;
import com.codeloam.memory.store.network.RequestProcessor;
import com.codeloam.memory.store.network.data.ChannelDataReader;
import com.codeloam.memory.store.network.data.ChannelDataWriter;
import com.codeloam.memory.store.network.data.DataReader;
import com.codeloam.memory.store.network.data.NetworkData;
import com.codeloam.memory.store.network.data.NetworkError;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.List;

/**
 * Server with java NIO. All operations are processed in current thread.
 *
 * @author jinyu.li
 * @since 1.0
 */
public class NioServer extends AbstractNioServer {
    public NioServer(String host, int port) {
        super(host, port);
    }

    public NioServer(String host, int port, DatabaseType type) {
        super(host, port, type);
    }

    public NioServer(String host, int port, RequestProcessor requestProcessor, DatabaseType type) {
        super(host, port, requestProcessor, type, 50);
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
