package com.codeloam.memory.store.network.data;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

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

    @Override
    public String toString() {
        return "NetworkInteger{"
                + "value=" + value
                + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NetworkInteger that)) {
            return false;
        }
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
