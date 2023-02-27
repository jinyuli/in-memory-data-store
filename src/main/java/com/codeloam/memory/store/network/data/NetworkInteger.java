package com.codeloam.memory.store.network.data;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Represents an integer(64 bits max).
 *
 * @author jinyu.li
 * @since 1.0
 */
public class NetworkInteger extends NetworkData {
    public static final byte[] PREFIX = ":".getBytes(StandardCharsets.UTF_8);
    private final long value;

    public NetworkInteger(long value) {
        this.value = value;
    }

    @Override
    public NetworkDataType getNetworkDataType() {
        return NetworkDataType.Integer;
    }

    @Override
    public void write(DataWriter writer) throws IOException {
        writer.write(PREFIX);
        writer.write(String.valueOf(value).getBytes(StandardCharsets.UTF_8));
        writer.write(END);
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
