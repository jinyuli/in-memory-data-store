package com.codeloam.memory.store.network.data;

import com.codeloam.memory.store.network.ByteWord;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

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
    private final ByteWord data;

    public NetworkBulkString(ByteWord data) {
        this.data = data;
    }

    @Override
    public NetworkDataType getNetworkDataType() {
        return NetworkDataType.BulkString;
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
        output.write('$');
        write(output, data.size());
        output.write(END);
        write(output, data);
        output.write(END);
    }
}
