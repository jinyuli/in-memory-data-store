package com.codeloam.memory.store.network.data;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * Represents an integer(64 bits max).
 *
 * @author jinyu.li
 * @since 1.0
 */
public class NetworkInteger extends NetworkData {
    private final long value;

    public NetworkInteger(long value) {
        this.value = value;
    }

    @Override
    public NetworkDataType getNetworkDataType() {
        return NetworkDataType.Integer;
    }

    @Override
    public void write(OutputStream output) throws IOException {
        output.write(':');
        output.write(String.valueOf(value).getBytes(StandardCharsets.UTF_8));
        output.write(END);
    }
}
