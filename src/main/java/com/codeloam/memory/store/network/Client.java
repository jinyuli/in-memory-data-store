package com.codeloam.memory.store.network;

import com.codeloam.memory.store.command.Command;
import com.codeloam.memory.store.measure.MeasureData;
import com.codeloam.memory.store.network.data.NetworkData;

import java.nio.channels.SocketChannel;

/**
 * Represents a client request.
 *
 * @author jinyu.li
 * @since 1.0
 */
public class Client {
    private final SocketChannel socketChannel;
    private MeasureData measureData;
    private Command command;
    private NetworkData result;
    private boolean clientError;

    public Client(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    public SocketChannel getSocketChannel() {
        return socketChannel;
    }

    public MeasureData getMeasureData() {
        return measureData;
    }

    public void setMeasureData(MeasureData measureData) {
        this.measureData = measureData;
    }

    public Command getCommand() {
        return command;
    }

    public void setCommand(Command command) {
        this.command = command;
    }

    public NetworkData getResult() {
        return result;
    }

    public void setResult(NetworkData result) {
        this.result = result;
    }

    public boolean isClientError() {
        return clientError;
    }

    public void setClientError(boolean clientError) {
        this.clientError = clientError;
    }
}
