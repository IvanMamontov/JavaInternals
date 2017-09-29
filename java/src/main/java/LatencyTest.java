package edu;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.VerboseMode;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 2, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 2, time = 3, timeUnit = TimeUnit.SECONDS)
@Fork(value = 1)
public class LatencyTest {

    public Random rand;

    @Param({"100", "1000000"})
    private int warm;

    @Setup(Level.Invocation)
    public void initI() {
        rand = new Random(0xDEAD_BEEF);
        busyPause(warm);
    }

    private long busyPause(long pauseInNanos) {
        Blackhole.consumeCPU(pauseInNanos);
        return pauseInNanos;
    }

    @Benchmark
    @BenchmarkMode({Mode.AverageTime})
    public long latencyBusyPauseShort() {
        return busyPause(100L);
    }

    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder()
                .include(LatencyTest.class.getName())
                .verbosity(VerboseMode.NORMAL)
                .build();
        new Runner(options).run();
    }
}