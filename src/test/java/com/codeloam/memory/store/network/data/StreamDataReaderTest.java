package com.codeloam.memory.store.network.data;

import com.codeloam.memory.store.command.InvalidCommandException;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

/**
 * Test StreamDataReader.
 *
 * @author jinyu.li
 * @since 1.0
 */
public class StreamDataReaderTest {

    @Test
    public void testConstructor() {
        InputStream stream = mock(InputStream.class);
        StreamDataReader reader = new StreamDataReader(stream, 32, 1024);
        assertNotNull(reader);
    }

    @Test
    public void testPeak() {
        byte[] buf = new byte[32];
        for (byte i = 0; i < 32; ++i) {
            buf[i] = i;
        }
        InputStream stream = new ByteArrayInputStream(buf);

        StreamDataReader reader = new StreamDataReader(stream, 32, 1024);

        try {
            byte b = reader.peek();
            assertEquals(b, buf[0], "Wrong byte");
            assertEquals(reader.getOffset(), 0, "Peek should not move offset");
        } catch (Throwable e) {
            // fail
            fail("should not throw exception", e);
        }
    }

    @Test
    public void testSkip() {
        byte[] buf = new byte[32];
        for (byte i = 0; i < 32; ++i) {
            buf[i] = i;
        }
        InputStream stream = new ByteArrayInputStream(buf);

        StreamDataReader reader = new StreamDataReader(stream, 32, 1024);

        try {
            reader.skip(2);
            assertEquals(reader.getOffset(), 2, "Skip() method should move offset");

            reader.read(28);
            assertEquals(reader.getOffset(), 30, "Read() method should move offset");

            reader.skip(2);
            assertEquals(reader.getOffset(), 32, "Skip() method should move offset");
        } catch (Throwable e) {
            // fail
            fail("should not throw exception", e);
        }
    }

    @Test
    public void testReadNormal() {
        byte[] buf = new byte[32];
        for (byte i = 0; i < 32; ++i) {
            buf[i] = i;
        }
        InputStream stream = new ByteArrayInputStream(buf);

        StreamDataReader reader = new StreamDataReader(stream, 32, 1024);

        try {
            int len = 10;
            byte[] data = reader.read(len);
            assertEquals(len, data.length, "There should be 10 elements");
            for (byte i = 0; i < len; ++i) {
                assertEquals(buf[i], data[i], "invalid byte");
            }
            assertEquals(len, reader.getOffset());

            // read again
            data = reader.read(len);
            assertEquals(len, data.length, "There should be 10 elements");
            for (byte i = 0; i < len; ++i) {
                assertEquals(buf[i + len], data[i], "invalid byte");
            }
            assertEquals(len + len, reader.getOffset());
        } catch (Throwable e) {
            // fail
            fail("should not throw exception", e);
        }
    }

    @Test
    public void testReadWithSmallBufferSize() {
        byte[] buf = new byte[32];
        for (byte i = 0; i < 32; ++i) {
            buf[i] = i;
        }
        InputStream stream = new ByteArrayInputStream(buf);

        // use a small buffer size
        StreamDataReader reader = new StreamDataReader(stream, 5, 1024);

        try {
            int len = 9;
            byte[] data = reader.read(len);
            assertEquals(len, data.length, "There should be 9 elements");
            for (byte i = 0; i < len; ++i) {
                assertEquals(buf[i], data[i], "invalid byte");
            }

            data = reader.read(len);
            assertEquals(len, data.length, "There should be 9 elements");
            for (byte i = 0; i < len; ++i) {
                assertEquals(buf[i + len], data[i], "invalid byte");
            }
        } catch (Throwable e) {
            // fail
            fail("should not throw exception", e);
        }
    }

    @Test
    public void testReadUntilStop() {
        byte[] buf = new byte[32];
        for (byte i = 0; i < 32; ++i) {
            buf[i] = 1;
        }
        buf[30] = '\r';
        buf[31] = '\n';
        InputStream stream = new ByteArrayInputStream(buf);

        // use a small buffer size
        StreamDataReader reader = new StreamDataReader(stream, 9, 1024);

        try {
            List<byte[]> data = reader.readUntilStop();
            assertFalse(data.isEmpty(), "There should be elements");
            assertEquals(4, data.size(), "There should be 6 elements");

            int total = data.stream().mapToInt(b -> b.length).sum();
            assertEquals(buf.length - 2, total, "There should be elements");
            byte[] d = new byte[total];
            int offset = 0;
            for (byte[] b : data) {
                System.arraycopy(b, 0, d, offset, b.length);
                offset += b.length;
            }
            for (byte i = 0; i < buf.length - 2; ++i) {
                assertEquals(buf[i], d[i], "invalid byte");
            }
        } catch (Throwable e) {
            // fail
            fail("should not throw exception", e);
        }
    }

    @Test
    public void testReadUntilStopWithSmallBufferSize() {
        byte[] buf = new byte[32];
        for (byte i = 0; i < 32; ++i) {
            buf[i] = 1;
        }
        buf[30] = '\r';
        buf[31] = '\n';
        InputStream stream = new ByteArrayInputStream(buf);

        // use a small buffer size
        StreamDataReader reader = new StreamDataReader(stream, 5, 1024);

        try {
            List<byte[]> data = reader.readUntilStop();
            assertFalse(data.isEmpty(), "There should be elements");
            assertEquals(6, data.size(), "There should be 6 elements");

            int total = data.stream().mapToInt(b -> b.length).sum();
            assertEquals(buf.length - 2, total, "There should be elements");
            byte[] d = new byte[total];
            int offset = 0;
            for (byte[] b : data) {
                System.arraycopy(b, 0, d, offset, b.length);
                offset += b.length;
            }
            for (byte i = 0; i < buf.length - 2; ++i) {
                assertEquals(buf[i], d[i], "invalid byte");
            }
        } catch (Throwable e) {
            // fail
            fail("should not throw exception", e);
        }
    }

    @Test
    public void testReadWithInvalidCommandException() {
        byte[] buf = new byte[32];
        for (byte i = 0; i < 32; ++i) {
            buf[i] = i;
        }
        InputStream stream = new ByteArrayInputStream(buf);

        StreamDataReader reader = new StreamDataReader(stream, 32, 4);

        try {
            int len = 10;
            reader.read(len);
            fail("should throw exception");
        } catch (InvalidCommandException e) {
            // pass
        } catch (IOException e) {
            fail("should not throw IOException");
        }
    }

    @Test
    public void testReadWithException() {
        InputStream stream = mock(InputStream.class);

        // use a small buffer size
        StreamDataReader reader = new StreamDataReader(stream, 5, 1024);

        try {
            byte[] buf = new byte[1];
            doThrow(IOException.class).when(stream).read(isA(buf.getClass()));
            int len = 10;
            byte[] data = reader.read(len);
            fail("should throw exception");
        } catch (IOException e) {
            // pass
        }
    }
}
