package com.codeloam.memory.store.network;

import com.codeloam.memory.store.JimdsException;
import com.codeloam.memory.store.command.Command;
import com.codeloam.memory.store.command.CommandFactory;
import com.codeloam.memory.store.database.Database;
import com.codeloam.memory.store.database.DatabaseFactory;
import com.codeloam.memory.store.database.DatabaseType;
import com.codeloam.memory.store.network.data.NetworkData;
import com.codeloam.memory.store.network.data.NetworkError;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.util.List;

/**
 * Base class for all Server.
 *
 * @author jinyu.li
 * @since 1.0
 */
public abstract class AbstractServer implements Server {
    /**
     * Buffer size for reading.
     */
    protected final int bufSize;

    /**
     * Database.
     */
    protected final Database database;

    /**
     * Request processor.
     */
    protected final RequestProcessor requestProcessor;

    /**
     * Constructor.
     *
     * @param type database type
     * @param requestProcessor request processor
     */
    public AbstractServer(DatabaseType type, RequestProcessor requestProcessor) {
        database = DatabaseFactory.create(type);
        this.requestProcessor = requestProcessor;
        this.bufSize = DEFAULT_BUF_SIZE;
    }


    protected void closeQuietly(Closeable c) {
        try {
            if (c != null) {
                c.close();
            }
        } catch (IOException e) {
            // ignore
            e.printStackTrace();
        }
    }
}
