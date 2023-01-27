package com.codeloam.memory.store.network;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * The processor that is used to process client request.
 *
 * @author jinyu.li
 * @since 1.0
 */
public class ClientRequestProcessor implements RequestProcessor {
    @Override
    public void process(InputStream inputStream) {
        ClientCommandReader clientCommandReader = new ClientCommandReader();
        try {
            List<ByteWord> command = clientCommandReader.read(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
