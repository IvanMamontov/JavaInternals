package edu.jvm.runtime.safepoint;

import edu.Utils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.profile.LinuxPerfAsmProfiler;
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
@Fork(value = 3, jvmArgs = {
        "-XX:+PrintSafepointStatistics",//print statistics about safepoint synchronization
        "-XX:PrintSafepointStatisticsTimeout=30",//print safepoint statistics only when safepoint takes more than 30 millis
        "-XX:-UseBiasedLocking"//disable biased locking in order to avoid redundant safepoint polling
})
@Warmup(iterations = 1, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 2, time = 20, timeUnit = TimeUnit.SECONDS)
public class ObserverEffectBench {

    @Benchmark
    @Group("observing")
    @GroupThreads(1)
    public double observed_slowpoke() {
        return Utils.slowpoke(2000000);
    }

    @Benchmark
    @Group("observing")
    @GroupThreads(1)
    public void observed_flash() {
        Blackhole.consumeCPU(10);
    }

    /**
     *
     */
    @Benchmark
    @Group("observing")
    @GroupThreads(1)
    public int sampler() throws Exception {
        Thread.sleep(20);
        Map<Thread, StackTraceElement[]> allStackTraces = Thread.getAllStackTraces();
        return allStackTraces.size();
    }

    @Benchmark
    @Group("free")
    @GroupThreads(1)
    public void free_flash() {
        Blackhole.consumeCPU(10);
    }

    @Benchmark
    @Group("free")
    @GroupThreads(1)
    public double free_slowpoke() {
        return Utils.slowpoke(2000000);
    }

    /**
     *
     */
    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder()
                .include(ObserverEffectBench.class.getName())
                .verbosity(VerboseMode.NORMAL)
                .addProfiler(LinuxPerfAsmProfiler.class)
                .build();
        new Runner(options).run();
    }
}