package com.codeloam.memory.store;

import com.codeloam.memory.store.network.Server;
import com.codeloam.memory.store.network.bio.BioServer;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test Server.
 *
 * @author jinyu.li
 * @since 1.0
 */
public class ServerTest {
    @Test
    public void testStringCommand() {
        String host = "localhost";
        int port = 3218;

        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(2);

        List<String> commands = CommandHelper.generateStringCommands(2);
        Server server = new BioServer(host, port);

        executorService.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                try {
                    server.start();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                System.out.println("server thread finished");
                return null;
            }
        });

        executorService.schedule(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                Client client = new Client(host, port);
                client.sendCommands(commands);
                System.out.println("commands are done, close server");
                server.close();
                System.out.println("client thread finished");
                return null;
            }
        }, 1000, TimeUnit.MILLISECONDS);

        try {
            executorService.shutdown();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
