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

/**
 * @author jinyu.li
 * @since 1.0
 */
@State(Scope.Thread)
public class BenchmarkTest {

    private final Client client = new Client("General", "localhost", 3128);
    private final List<List<byte[]>> fullCommands = new ArrayList<>();
    private final List<List<byte[]>> fullSmallSizeCommands = new ArrayList<>();
    private final List<List<byte[]>> commands = new ArrayList<>();
    private final List<List<byte[]>> smallSizeCommands = new ArrayList<>();

    private Random random;
    private int index;

    @Setup
    public void setup() {
        random = new Random();
        fullCommands.addAll(CommandHelper.generateStringCommands(1000, 10240));
        fullSmallSizeCommands.addAll(CommandHelper.generateStringCommands(1000, 1024));
    }

    @Setup(Level.Invocation)
    public void setupInvocation() {
        index = random.nextInt(fullCommands.size()/2);
    }

    @Benchmark
    @BenchmarkMode(Mode.All)
    public void testStringGetSetSmallSize() throws IOException {
        client.sendCommands(fullSmallSizeCommands.subList(index*2, index*2+2));
    }

    @Benchmark
    @BenchmarkMode(Mode.All)
    public void testStringGetSet() throws IOException {
        client.sendCommands(fullCommands.subList(index*2, index*2+2));
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
