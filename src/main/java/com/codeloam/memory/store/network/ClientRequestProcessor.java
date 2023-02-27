package com.codeloam.memory.store.network;

import com.codeloam.memory.store.JimdsException;
import com.codeloam.memory.store.command.Command;
import com.codeloam.memory.store.command.CommandFactory;
import com.codeloam.memory.store.command.InvalidCommandException;
import com.codeloam.memory.store.database.Database;
import com.codeloam.memory.store.network.data.NetworkData;
import com.codeloam.memory.store.network.data.NetworkError;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * The processor that is used to process client request.
 *
 * @author jinyu.li
 * @since 1.0
 */
public class ClientRequestProcessor implements RequestProcessor {

    public ClientRequestProcessor() {
    }

    @Override
    public NetworkData process(Database database, Command command) throws IOException {
        if (command == null) {
            throw new InvalidCommandException("Unknown command");
        }
        return database.execute(command);
    }
}
