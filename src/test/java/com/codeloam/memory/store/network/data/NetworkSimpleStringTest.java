package com.codeloam.memory.store.network.data;

import com.codeloam.memory.store.network.ByteWord;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

/**
 * Test NetworkSimpleString.
 *
 * @author jinyu.li
 * @since 1.0
 */
public class NetworkSimpleStringTest {

    @Test
    public void testWrite() {
        String string = "test";
        NetworkSimpleString data = new NetworkSimpleString(ByteWord.create(string));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            data.write(outputStream);
            byte[] result = outputStream.toByteArray();
            int index = 0;
            assertEquals('+', result[index++], "+");
            byte[] expected = string.getBytes(StandardCharsets.UTF_8);
            for (int i = 0; i < expected.length; ++i) {
                assertEquals(expected[i], result[index++], "position " + i);
            }
            assertEquals('\r', result[index++], "CR");
            assertEquals('\n', result[index++], "LF");
            assertEquals(index, result.length);
        } catch (IOException e) {
            fail("Should not throw exception");
        }
    }

    @Test
    public void testWriteWithException() {
        String string = "test";
        NetworkSimpleString data = new NetworkSimpleString(ByteWord.create(string));
        OutputStream outputStream = mock(OutputStream.class);
        try {
            doThrow(new IOException()).when(outputStream).write(any());
            doThrow(new IOException()).when(outputStream).write(any(), anyInt(), anyInt());
        } catch (IOException e) {
            fail("Should not throw exception");
        }
        try {
            data.write(outputStream);
            fail("Should throw exception");
        } catch (IOException e) {
            // pass
        }
    }
}
