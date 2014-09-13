package edu.jvm.runtime.safepoint;

import org.openjdk.jmh.annotations.*;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@State(Scope.Group)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@Fork(value = 3, jvmArgs = {"-XX:+PrintSafepointStatistics", "-XX:PrintSafepointStatisticsTimeout=30", "-XX:-UseBiasedLocking"})
@Warmup(iterations = 1, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 2, time = 20, timeUnit = TimeUnit.SECONDS)
public class SafepointBench {

    @Benchmark
    @Group("g")
    @GroupThreads(1)
    public double slowpoke() {
        double d = 0;
        for (int j = 1; j < 2000000; j++) {
            d += Math.log(Math.E * j);
        }
        return d;
    }

    @Benchmark
    @Group("g")
    @GroupThreads(1)
    public double flash() {
        return System.nanoTime();
    }

    @Benchmark
    @Group("g")
    @GroupThreads(1)
    public int tracer() throws Exception {
        Thread.sleep(20);
        Map<Thread, StackTraceElement[]> allStackTraces = Thread.getAllStackTraces();
        return allStackTraces.size();
    }
}
