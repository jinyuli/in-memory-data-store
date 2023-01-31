package com.codeloam.memory.store.database.result;

import com.codeloam.memory.store.network.ByteWord;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Represents an error messages.
 *
 * @author jinyu.li
 * @since 1.0
 */
public class ErrorResult extends JimdsResult {
    private final ByteWord error;

    public ErrorResult(String msg) {
        this.error = ByteWord.create(msg);
    }

    public ErrorResult(ByteWord error) {
        this.error = error;
    }

    @Override
    public ResultType getResultType() {
        return ResultType.Error;
    }

    @Override
    public void write(OutputStream output) throws IOException {
        output.write('-');
    }
}
