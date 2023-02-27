package com.codeloam.memory.store.measure;

/**
 * Measure collector for requests.
 *
 * @author jinyu.li
 * @since 1.0
 */
public class RequestMeasureCollector extends MeasureCollector {
    public static final String FIELD_NAME_CONNECTED = "connected";
    public static final String FIELD_NAME_READ = "read";
    public static final String FIELD_NAME_PARSE = "parse";
    public static final String FIELD_NAME_PROCESS = "process";
    public static final String FIELD_NAME_SEND = "send";
    public static final String FIELD_NAME_ERROR = "error";

    public RequestMeasureCollector(String name) {
        super(name);
    }

    @Override
    public void print() {
        int errCount = 0;
        long count = 0;
        // 0: total, 1: read, 2: parse, 3: process, 4:send
        double[] totals = new double[5];
        double[] maxes = new double[5];
        for (MeasureData data : dataList) {
            if (data.get(FIELD_NAME_ERROR) != null) {
                errCount++;
            } else {
                double start = data.get(FIELD_NAME_CONNECTED);
                double read = data.get(FIELD_NAME_READ);
                double send = data.get(FIELD_NAME_SEND);
                totals[0] += send - start;
                totals[1] += read - start;

                double parse = data.get(FIELD_NAME_PARSE);
                totals[2] += parse - read;

                double process = data.get(FIELD_NAME_PROCESS);
                totals[3] += process - parse;
                totals[4] += send - process;
                if (send - start > maxes[0]) {
                    maxes[0] = send - start;
                }
                if (read - start > maxes[1]) {
                    maxes[1] = read - start;
                }
                if (parse - read > maxes[2]) {
                    maxes[2] = parse - read;
                }
                if (process - parse > maxes[3]) {
                    maxes[3] = process - parse;
                }
                if (send - process > maxes[4]) {
                    maxes[4] = send - process;
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
        sb.append(String.format("avg        : %.2f ms, max: %.2f ms\n", totals[0] / count, maxes[0]));
        sb.append(String.format("avg read   : %.2f ms, max: %.2f ms\n", totals[1] / count, maxes[1]));
        sb.append(String.format("avg parse  : %.2f ms, max: %.2f ms\n", totals[2] / count, maxes[2]));
        sb.append(String.format("avg process: %.2f ms, max: %.2f ms\n", totals[3] / count, maxes[3]));
        sb.append(String.format("avg send   : %.2f ms, max: %.2f ms\n", totals[4] / count, maxes[4]));
        System.out.println(sb.toString());

        MeasureData data = new MeasureData();
        data.add(FIELD_NAME_CONNECTED, 0);
        data.add(FIELD_NAME_READ, totals[1] / count);
        data.add(FIELD_NAME_PARSE, (totals[2] + totals[1]) / count);
        data.add(FIELD_NAME_PROCESS, (totals[3] + totals[2] + totals[1]) / count);
        data.add(FIELD_NAME_SEND, (totals[4] + totals[3] + totals[2] + totals[1]) / count);

        dataList.clear();
        dataList.add(data);
    }
}
