package com.codeloam.memory.store.database.simple.executor;

import com.codeloam.memory.store.command.Command;
import com.codeloam.memory.store.database.Database;
import com.codeloam.memory.store.database.JimdsData;
import com.codeloam.memory.store.database.JimdsHash;
import com.codeloam.memory.store.network.ByteWord;
import com.codeloam.memory.store.network.data.NetworkData;

/**
 * @author jinyu.li
 * @since 1.0
 */
public interface Executor {
    NetworkData execute(JimdsHash<ByteWord, JimdsData> database, Command command);
}
