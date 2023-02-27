package com.codeloam.memory.store;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Pressure Test.
 *
 * @author jinyu.li
 * @since 1.0
 */
public class PressureTest {
    private static final int K = 1024;
    private static final int M = 1024 * K;

    public static void main(String[] args) {
        PressureTest test = new PressureTest();
        test.testStringCommand();
    }

//    @Disabled("manual only")
//    @Test
    public void testStringCommand() {
//        Thread.sleep(10000000);
        String host = "localhost";
        int port = 3128;

        ExecutorService executorService = Executors.newCachedThreadPool(new NamedThreadFactory("Pressure"));
        Queue<ClientMeasureCollector> collectors = new LinkedBlockingQueue<>();

        int[] byteSizes = new int[]{50, 100, 1024, 5 * K, 10 * K, 100 * K};
        for (int byteSize : byteSizes) {
            System.out.println("Generated commands");
            List<List<byte[]>> commands = CommandHelper.generateStringCommands(100, byteSize);
            System.out.println("Commands generated");
            executorService.submit(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    System.out.println("start client thread");
                    Client client = new Client(String.format("Client(%d)", byteSize), host, port);
                    client.sendCommands(commands);
                    System.out.printf("commands for byte size(%d) are done, close server\n", byteSize);
                    System.out.println("client thread finished");
                    collectors.offer(client.getCollector());
                    return null;
                }
            });
        }

        int[] bigByteSize = new int[]{M, 2 * M, 5 * M};
        for (int byteSize : bigByteSize) {
            System.out.println("Generated commands");
            List<List<byte[]>> commands = CommandHelper.generateStringCommands(50, byteSize);
            System.out.println("Commands generated");
            executorService.submit(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    Client client = new Client(String.format("Client(%d)", byteSize), host, port);
                    client.sendCommands(commands);
                    System.out.printf("commands for big byte size(%dM) are done, close server\n", byteSize / M);
                    System.out.println("client thread finished");
                    collectors.offer(client.getCollector());
                    return null;
                }
            });
        }

        int[] shareByteSize = new int[]{5 * M, 10 * M, 50 * M};
        for (int byteSize : shareByteSize) {
            System.out.println("Generated commands");
            List<List<byte[]>> commands = CommandHelper.generateSharedStringCommands(5, byteSize);
            System.out.println("Commands generated");
            executorService.submit(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    Client client = new Client(String.format("Client(%d)", byteSize), host, port);
                    client.sendCommands(commands);
                    System.out.printf("commands for big byte size(%dM) are done, close server\n", byteSize / M);
                    System.out.println("client thread finished");
                    collectors.offer(client.getCollector());
                    return null;
                }
            });
        }

        try {
            int time = 40;
            executorService.shutdown();
            while (!executorService.awaitTermination(10, TimeUnit.SECONDS) && time >= 0) {
                time--;
            }
            if (time < 0) {
                executorService.shutdownNow();
            }
            ClientMeasureCollector totalCollector = new ClientMeasureCollector("Total");
            for (ClientMeasureCollector collector: collectors) {
                collector.print();
                totalCollector.addAll(collector.getData());
            }
            totalCollector.print();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
