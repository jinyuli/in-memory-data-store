package com.codeloam.memory.store;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
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

    public Client(String name, String host, int port) {
        this.name = name;
        this.host = host;
        this.port = port;
    }

    public void sendCommands(List<String> commandList) throws IOException {
        int count = 0;
        for (String command : commandList) {
            System.out.printf("%s sends out %dth command\n", name, count++);
            try (Socket socket = new Socket(host, port);
                InputStream input = socket.getInputStream();
                OutputStream output = socket.getOutputStream()) {
                output.write(command.getBytes(StandardCharsets.UTF_8));
                String result = readResult(input);
//                System.out.printf("%s\n", result);
            } catch (IOException e) {
                e.printStackTrace();
                throw e;
            }
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
