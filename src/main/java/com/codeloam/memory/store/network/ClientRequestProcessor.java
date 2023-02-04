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
    private final CommandReader commandReader;

    public ClientRequestProcessor(CommandReader commandReader) {
        this.commandReader = commandReader;
    }

    @Override
    public void process(Database database, InputStream inputStream, OutputStream outputStream) throws IOException {
        try {
            List<ByteWord> words = commandReader.read(inputStream);
            Command command = CommandFactory.parseCommand(words);
            if (command == null) {
                throw new InvalidCommandException("Unknown command");
            }
            NetworkData result = database.execute(command);
            writeResult(outputStream, result);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (JimdsException e) {
            // internal exception, write to output
            writeResult(outputStream, new NetworkError(e.getMessage()));
        }
    }

    private void writeResult(OutputStream outputStream, NetworkData data) throws IOException {
        data.write(outputStream);
    }
}
