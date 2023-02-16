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
    }

    /**
     * Send given commands to server.
     *
     * @param commandList commands
     * @throws IOException throw by Socket
     */
    public void sendCommands(List<List<byte[]>> commandList) throws IOException {
        MeasureCollector collector = new MeasureCollector();
        for (List<byte[]> command : commandList) {
            final MeasureData data = new MeasureData();
            data.add("start", System.currentTimeMillis());
            try (Socket socket = new Socket(host, port);
                 InputStream input = socket.getInputStream();
                 OutputStream output = socket.getOutputStream()) {
                data.add("connect", System.currentTimeMillis());
                for (byte[] bytes : command) {
                    output.write(bytes);
                }
                data.add("send", System.currentTimeMillis());
                readResult(input);
                data.add("read", System.currentTimeMillis());
            } catch (IOException e) {
                data.add("error", System.currentTimeMillis());
                e.printStackTrace();
//                throw e;
            }
            collector.add(data);
        }
        stat(collector);
    }

    private void stat(MeasureCollector collector) {
        int errCount = 0;
        long count = 0, totalTime = 0, totalConnect = 0, totalSend = 0, totalRead = 0;
        long maxTime = 0, maxConnect = 0, maxSend = 0, maxRead = 0;
        for (MeasureData data: collector.getDataList()) {
            if (data.get("error") != null) {
                errCount++;
            } else {
                long read = data.get("read");
                long send = data.get("send");
                long connect = data.get("connect");
                long start = data.get("start");
                totalTime += read - start;
                totalRead = read - send;
                totalSend = send - connect;
                totalConnect += connect - start;
                if (connect - start > maxConnect) {
                    maxConnect = connect - start;
                }
                if (send - connect > maxSend) {
                    maxSend = send - connect;
                }
                if (read - send > maxRead) {
                    maxRead = read - send;
                }
                if (read - start > maxTime) {
                    maxTime = read - start;
                }
                count++;
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("statistic for %s\n", name));
        sb.append(String.format("error count: %d\n", errCount));
        if (count == 0) {
            sb.append("No valid command\n");
            System.out.println(sb.toString());
            return;
        }
        sb.append(String.format("avg        : %.2f, max: %d\n", totalTime*1.0/count, maxTime));
        sb.append(String.format("avg connect: %.2f, max: %d\n", totalConnect*1.0/count, maxConnect));
        sb.append(String.format("avg send   : %.2f, max: %d\n", totalSend*1.0/count, maxSend));
        sb.append(String.format("avg read   : %.2f, max: %d\n", totalRead*1.0/count, maxRead));
        System.out.println(sb.toString());
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
