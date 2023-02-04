package com.codeloam.memory.store.network.data;

import com.codeloam.memory.store.network.ByteWord;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

/**
 * Represent Error data. Data should not include \r\n.
 *
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
        write(output, error);
        output.write(END);
    }

    @Override
    public String toString() {
        return "NetworkError{"
                + "error=" + error
                + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NetworkError that)) {
            return false;
        }
        return Objects.equals(error, that.error);
    }

    @Override
    public int hashCode() {
        return Objects.hash(error);
    }
}
