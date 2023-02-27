package com.codeloam.memory.store.network.data;

import com.codeloam.memory.store.network.ByteWord;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Represents a binary-safe string.
 *
 * @author jinyu.li
 * @since 1.0
 */
public class NetworkBulkString extends NetworkData {
    public static NetworkData NULL = new NetworkBulkString(null);

    private static final byte[] NULL_BYTES = "$-1\r\n".getBytes(StandardCharsets.UTF_8);
    private static final byte[] EMPTY_BYTES = "$0\r\n\r\n".getBytes(StandardCharsets.UTF_8);
    public static final byte[] PREFIX = "$".getBytes(StandardCharsets.UTF_8);
    private final ByteWord data;

    public NetworkBulkString(ByteWord data) {
        this.data = data;
    }

    @Override
    public NetworkDataType getNetworkDataType() {
        return NetworkDataType.BulkString;
    }

    @Override
    public void write(DataWriter writer) throws IOException {
        if (data == null) {
            writer.write(NULL_BYTES);
            return;
        }
        if (data.size() == 0) {
            writer.write(EMPTY_BYTES);
            return;
        }
        writer.write(PREFIX);
        write(writer, data.size());
        writer.write(END);
        write(writer, data);
        writer.write(END);
    }

    @Override
    public String toString() {
        return "NetworkBulkString{"
                + "data=" + data
                + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NetworkBulkString that)) {
            return false;
        }
        return Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(data);
    }
}
