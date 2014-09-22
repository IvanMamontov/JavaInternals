package edu.jvm.runtime.safepoint;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.VerboseMode;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@State(Scope.Group)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(value = 3, jvmArgs = {"-XX:+PrintSafepointStatistics", "-XX:PrintSafepointStatisticsTimeout=30", "-XX:-UseBiasedLocking"})
@Warmup(iterations = 1, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 2, time = 20, timeUnit = TimeUnit.SECONDS)
public class SafepointBench {

    int i = 0;

    @Benchmark
    @Group("g")
    @GroupThreads(1)
    public double slowpoke() {
        double d = 0;
        //no chance to unroll
        for (int j = 1; j < 2000000; j++) {
            d += Math.log(Math.E * j);
        }
        return d;
    }

    @Benchmark
    @Group("g")
    @GroupThreads(1)
    public void flash() {
        i++;
    }

    @Benchmark
    @Group("g")
    @GroupThreads(1)
    public int sampler() throws Exception {
        Thread.sleep(20);
        Map<Thread, StackTraceElement[]> allStackTraces = Thread.getAllStackTraces();
        return allStackTraces.size();
    }

    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder()
                .include(SafepointBench.class.getName())
                .verbosity(VerboseMode.NORMAL)
                .build();
        new Runner(options).run();
    }
}
