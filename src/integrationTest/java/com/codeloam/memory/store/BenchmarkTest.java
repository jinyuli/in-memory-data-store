package com.codeloam.memory.store;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author jinyu.li
 * @since 1.0
 */
@State(Scope.Thread)
public class BenchmarkTest {
    private static final int SIZE = 1000;
    private final Client client = new Client("General", "localhost", 3128);
    private final List<List<byte[]>> fullCommands = new ArrayList<>();
    private final List<List<byte[]>> fullSmallSizeCommands = new ArrayList<>();
    private final AtomicInteger index = new AtomicInteger(0);

    @Setup
    public void setup() {
        fullCommands.addAll(CommandHelper.generateStringCommands(SIZE, 10240));
        fullSmallSizeCommands.addAll(CommandHelper.generateStringCommands(SIZE, 1024));
    }

    @Benchmark
    @BenchmarkMode(Mode.All)
    public void testStringGetSetSmallSize() throws IOException {
        int start = (index.incrementAndGet() % SIZE)/2;
        client.sendCommands(fullSmallSizeCommands.subList(start*2, start*2+2));
    }

    @Benchmark
    @BenchmarkMode(Mode.All)
    public void testStringGetSet() throws IOException {
        int start = (index.incrementAndGet() % SIZE)/2;
        client.sendCommands(fullCommands.subList(start*2, start*2+2));
    }

    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder()
                .include(BenchmarkTest.class.getSimpleName())
                .threads(4)
                .forks(1)
                .build();
        new Runner(options).run();
    }
}
