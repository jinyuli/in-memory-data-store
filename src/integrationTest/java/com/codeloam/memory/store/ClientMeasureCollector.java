package com.codeloam.memory.store;

import com.codeloam.memory.store.measure.MeasureCollector;
import com.codeloam.memory.store.measure.MeasureData;

/**
 * @author jinyu.li
 * @since 1.0
 */
public class ClientMeasureCollector extends MeasureCollector {
    public static final String FIELD_NAME_START = "start";
    public static final String FIELD_NAME_READ = "read";
    public static final String FIELD_NAME_SEND = "send";
    public static final String FIELD_NAME_CONNECT = "connect";
    public static final String FIELD_NAME_ERROR = "error";

    public ClientMeasureCollector(String name) {
        super(name);
    }

    public void print() {
        int errCount = 0;
        long count = 0;
        double totalTime = 0, totalConnect = 0, totalSend = 0, totalRead = 0;
        double maxTime = 0, maxConnect = 0, maxSend = 0, maxRead = 0;
        for (MeasureData data : dataList) {
            if (data.get(FIELD_NAME_ERROR) != null) {
                errCount++;
            } else {
                double start = data.get(FIELD_NAME_START);
                double connect = data.get(FIELD_NAME_CONNECT);
                double send = data.get(FIELD_NAME_SEND);
                double read = data.get(FIELD_NAME_READ);
                totalTime += read - start;
                totalRead += read - send;
                totalSend += send - connect;
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
        sb.append(String.format("statistic for %s with %d times\n", name, count + errCount));
        sb.append(String.format("error count: %d\n", errCount));
        if (count == 0) {
            sb.append("No valid command\n");
            System.out.println(sb.toString());
            return;
        }
        sb.append(String.format("avg        : %.2f ms, max: %.2f ms\n", totalTime / count, maxTime));
        sb.append(String.format("avg connect: %.2f ms, max: %.2f ms\n", totalConnect / count, maxConnect));
        sb.append(String.format("avg send   : %.2f ms, max: %.2f ms\n", totalSend / count, maxSend));
        sb.append(String.format("avg read   : %.2f ms, max: %.2f ms\n", totalRead / count, maxRead));
        System.out.println(sb.toString());
    }
}
