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
 * Test NetworkBulkString.
 *
 * @author jinyu.li
 * @since 1.0
 */
public class NetworkBulkStringTest {

    @Test
    public void testWrite() {
        String string = "testtesttesttest test";
        NetworkData data = new NetworkBulkString(ByteWord.create(string));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            byte[] expectedBytes = string.getBytes(StandardCharsets.UTF_8);
            byte[] expectedLenBytes = String.valueOf(expectedBytes.length).getBytes(StandardCharsets.UTF_8);
            data.write(new StreamDataWriter(outputStream));
            byte[] result = outputStream.toByteArray();
            int index = 0;
            assertEquals('$', result[index++], "$");
            for (int i = 0; i < expectedLenBytes.length; ++i) {
                assertEquals(expectedLenBytes[i], result[index++], "len position " + i);
            }
            assertEquals('\r', result[index++], "len CR");
            assertEquals('\n', result[index++], "len LF");

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
    public void testWriteNull() {
        NetworkData data = NetworkBulkString.NULL;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            byte[] expectedBytes = "$-1\r\n".getBytes(StandardCharsets.UTF_8);
            data.write(new StreamDataWriter(outputStream));
            byte[] result = outputStream.toByteArray();

            assertEquals(expectedBytes.length, result.length);
            for (int i = 0; i < expectedBytes.length; ++i) {
                assertEquals(expectedBytes[i], result[i], "position " + i);
            }
        } catch (IOException e) {
            fail("Should not throw exception");
        }
    }

    @Test
    public void testWriteWithException() {
        String string = "test";
        NetworkData data = new NetworkBulkString(ByteWord.create(string));
        OutputStream outputStream = mock(OutputStream.class);
        try {
            doThrow(new IOException()).when(outputStream).write(any());
            doThrow(new IOException()).when(outputStream).write(any(), anyInt(), anyInt());
        } catch (IOException e) {
            fail("Should not throw exception");
        }
        try {
            data.write(new StreamDataWriter(outputStream));
            fail("Should throw exception");
        } catch (IOException e) {
            // pass
        }
    }
}
