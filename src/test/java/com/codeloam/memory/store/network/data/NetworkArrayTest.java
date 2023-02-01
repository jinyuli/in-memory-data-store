package com.codeloam.memory.store.network.data;

import com.codeloam.memory.store.network.ByteWord;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

/**
 * Test NetworkArray.
 *
 * @author jinyu.li
 * @since 1.0
 */
public class NetworkArrayTest {

    @Test
    public void testWrite() {
        String string = "Error message";
        long value = 12345;
        NetworkData error = new NetworkError(ByteWord.create(string));
        NetworkData simple = new NetworkSimpleString(ByteWord.create(string));
        NetworkData bulk = new NetworkBulkString(ByteWord.create(string));
        NetworkData integer = new NetworkInteger(value);
        List<NetworkData> dataList = List.of(error, simple, bulk, integer);
        NetworkData data = new NetworkArray(dataList);

        byte[] expectedLongBytes = String.valueOf(value).getBytes(StandardCharsets.UTF_8);
        byte[] expected = string.getBytes(StandardCharsets.UTF_8);
        byte[] expectedLenBytes = String.valueOf(expected.length).getBytes(StandardCharsets.UTF_8);
        byte[] expectedArrayLenBytes = String.valueOf(dataList.size()).getBytes(StandardCharsets.UTF_8);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            data.write(outputStream);

            byte[] result = outputStream.toByteArray();
            int index = 0;
            assertEquals('*', result[index++], "prefix *");

            // validate length
            for (int i = 0; i < expectedArrayLenBytes.length; ++i) {
                assertEquals(expectedArrayLenBytes[i], result[index++], "position " + i);
            }
            assertEquals('\r', result[index++], "CR");
            assertEquals('\n', result[index++], "LF");

            // validate error
            assertEquals('-', result[index++], "prefix -");
            for (int i = 0; i < expected.length; ++i) {
                assertEquals(expected[i], result[index++], "position " + i);
            }
            assertEquals('\r', result[index++], "CR");
            assertEquals('\n', result[index++], "LF");

            // simple string
            assertEquals('+', result[index++], "+");
            for (int i = 0; i < expected.length; ++i) {
                assertEquals(expected[i], result[index++], "position " + i);
            }
            assertEquals('\r', result[index++], "CR");
            assertEquals('\n', result[index++], "LF");

            // bulk string
            assertEquals('$', result[index++], "$");
            for (int i = 0; i < expectedLenBytes.length; ++i) {
                assertEquals(expectedLenBytes[i], result[index++], "len position " + i);
            }
            assertEquals('\r', result[index++], "len CR");
            assertEquals('\n', result[index++], "len LF");

            for (int i = 0; i < expected.length; ++i) {
                assertEquals(expected[i], result[index++], "position " + i);
            }
            assertEquals('\r', result[index++], "CR");
            assertEquals('\n', result[index++], "LF");

            // integer
            assertEquals(':', result[index++], "prefix :");
            for (int i = 0; i < expectedLongBytes.length; ++i) {
                assertEquals(expectedLongBytes[i], result[index++], "position " + i);
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
        NetworkData data = new NetworkArray(null);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            byte[] expectedBytes = "*-1\r\n".getBytes(StandardCharsets.UTF_8);
            data.write(outputStream);
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
        NetworkData error = new NetworkError(ByteWord.create(string));
        NetworkData data = new NetworkArray(List.of(error));
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
