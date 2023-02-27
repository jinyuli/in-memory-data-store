package com.codeloam.memory.store;

import com.codeloam.memory.store.measure.MeasureData;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * A helper class, that sends commands to server.
 *
 * @author jinyu.li
 * @since 1.0
 */
public class Client {
    private final String name;
    private final String host;
    private final int port;

    private final ClientMeasureCollector collector;

    /**
     * Constructor.
     *
     * @param name client name
     * @param host server host
     * @param port server port
     */
    public Client(String name, String host, int port) {
        this.name = name;
        this.host = host;
        this.port = port;
        collector = new ClientMeasureCollector(name);
    }

    public ClientMeasureCollector getCollector() {
        return collector;
    }

    /**
     * Send given commands to server.
     *
     * @param commandList commands
     * @throws IOException throw by Socket
     */
    public void sendCommands(List<List<byte[]>> commandList) throws IOException {
        for (List<byte[]> command : commandList) {
            final MeasureData data = new MeasureData();
            data.add(ClientMeasureCollector.FIELD_NAME_START);
            try (Socket socket = new Socket(host, port);
                 InputStream input = socket.getInputStream();
                 OutputStream output = socket.getOutputStream()) {
                data.add(ClientMeasureCollector.FIELD_NAME_CONNECT);
                for (byte[] bytes : command) {
                    output.write(bytes);
                }
                data.add(ClientMeasureCollector.FIELD_NAME_SEND);
                readResult(input);
                data.add(ClientMeasureCollector.FIELD_NAME_READ);
            } catch (IOException e) {
                data.add(ClientMeasureCollector.FIELD_NAME_ERROR);
                System.err.println(name);
                e.printStackTrace();
//                throw e;
            }
            collector.add(data);
        }
    }



    private String readResult(InputStream input) throws IOException {
        byte[] buf = new byte[32];
        int count = 0;
        List<byte[]> list = new ArrayList<>();
        int len = 0;
        while ((count = input.read(buf)) >= 0) {
            if (count > 0) {
                byte[] tmp = new byte[count];
                System.arraycopy(buf, 0, tmp, 0, count);
                list.add(tmp);
                len += count;
            }
        }
        if (len > 0) {
            byte[] strBuf = new byte[len];
            int offset = 0;
            for (byte[] ba : list) {
                System.arraycopy(ba, 0, strBuf, offset, ba.length);
                offset += ba.length;
            }
            return new String(strBuf);
        } else {
            return null;
        }

    }
}
