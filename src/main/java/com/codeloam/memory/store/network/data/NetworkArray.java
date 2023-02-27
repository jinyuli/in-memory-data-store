package com.codeloam.memory.store.network.data;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

/**
 * Array in RESP.
 *
 * @author jinyu.li
 * @since 1.0
 */
public class NetworkArray extends NetworkData {
    private static final byte[] EMPTY_BYTES = "*0\r\n".getBytes(StandardCharsets.UTF_8);
    private static final byte[] NULL_BYTES = "*-1\r\n".getBytes(StandardCharsets.UTF_8);
    public static final byte[] PREFIX = "*".getBytes(StandardCharsets.UTF_8);
    private final List<NetworkData> data;

    public NetworkArray(List<NetworkData> data) {
        this.data = data;
    }

    @Override
    public NetworkDataType getNetworkDataType() {
        return NetworkDataType.Array;
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
        for (NetworkData d : data) {
            d.write(writer);
        }
    }

    @Override
    public String toString() {
        return "NetworkArray{"
                + "data=" + data
                + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NetworkArray that)) {
            return false;
        }
        return Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(data);
    }
}
