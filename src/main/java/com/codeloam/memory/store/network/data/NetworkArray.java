package com.codeloam.memory.store.network.data;

import java.io.IOException;
import java.io.OutputStream;
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
    private final List<NetworkData> data;

    public NetworkArray(List<NetworkData> data) {
        this.data = data;
    }

    @Override
    public NetworkDataType getNetworkDataType() {
        return NetworkDataType.Array;
    }

    @Override
    public void write(OutputStream output) throws IOException {
        if (data == null) {
            output.write(NULL_BYTES);
            return;
        }
        if (data.size() == 0) {
            output.write(EMPTY_BYTES);
            return;
        }
        output.write('*');
        write(output, data.size());
        output.write(END);
        for (NetworkData d : data) {
            d.write(output);
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
