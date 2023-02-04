package com.codeloam.memory.store.network.data;

import com.codeloam.memory.store.network.ByteWord;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

/**
 * Represent simple string data. Data should not include \r\n.
 *
 * @author jinyu.li
 * @since 1.0
 */
public class NetworkSimpleString extends NetworkData {
    public static final NetworkData OK = new NetworkSimpleString(ByteWord.create("OK"));

    private final ByteWord data;

    public NetworkSimpleString(ByteWord data) {
        this.data = data;
    }

    @Override
    public NetworkDataType getNetworkDataType() {
        return NetworkDataType.SimpleString;
    }

    @Override
    public void write(OutputStream output) throws IOException {
        output.write('+');
        write(output, data);
        output.write(END);
    }

    @Override
    public String toString() {
        return "NetworkSimpleString{"
                + "data=" + data
                + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NetworkSimpleString that)) {
            return false;
        }
        return Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(data);
    }
}
