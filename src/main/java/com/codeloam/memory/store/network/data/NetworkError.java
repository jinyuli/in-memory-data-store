package com.codeloam.memory.store.network.data;

import com.codeloam.memory.store.network.ByteWord;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author jinyu.li
 * @since 1.0
 */
public class NetworkError extends NetworkData {
    private final ByteWord error;

    public NetworkError(String msg) {
        this.error = ByteWord.create(msg);
    }

    public NetworkError(ByteWord error) {
        this.error = error;
    }

    @Override
    public NetworkDataType getNetworkDataType() {
        return NetworkDataType.Error;
    }

    @Override
    public void write(OutputStream output) throws IOException {
        output.write('-');
        output.write(END);
    }
}
