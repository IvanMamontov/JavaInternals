package edu.jvm.runtime.safepoint;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.profile.HotspotRuntimeProfiler;
import org.openjdk.jmh.profile.StackProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;
import org.openjdk.jmh.runner.options.VerboseMode;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@Fork(value = 3, jvmArgs = {"-XX:-UseBiasedLocking", "-Djmh.stack.lines=4"})
@Warmup(iterations = 1, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 2, time = 20, timeUnit = TimeUnit.SECONDS)
public class ProfilerBench {

    public static final int ITERATIONS_COUNT = Integer.MAX_VALUE >> 5;
    int[] array = new int[1024];

    public void hot(int i) {
        int ii = (i + 10 * 100) % array.length;
        int jj = (ii + i / 33) % array.length;
        if (ii < 0) ii = -ii;
        if (jj < 0) jj = -jj;
        array[ii] = array[jj] + 1;
    }

    @CompilerControl(value = CompilerControl.Mode.EXCLUDE)
    public void hotWithSafepoint(int i) {
        hot(i);
    }

    @Benchmark
    public void cold() {
        for (int i = 0; i < ITERATIONS_COUNT; i++) {
            hot(i);
        }
    }

    @Benchmark
    public void coldWithSafepoint() {
        for (long i = 0; i < ITERATIONS_COUNT; i++) {
            //safepoint is here
            hot((int) i);
        }
    }

    @Benchmark
    public void coldWithSafepoint2() {
        for (long i = 0; i < ITERATIONS_COUNT; i++) {
            //safepoint is here
            hotWithSafepoint((int) i);
        }
    }

    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder()
                .include(ProfilerBench.class.getName())
                .warmupTime(TimeValue.seconds(1))
                .measurementTime(TimeValue.seconds(10))
                .warmupIterations(2)
                .addProfiler(StackProfiler.class)
                .addProfiler(HotspotRuntimeProfiler.class)
                .measurementIterations(1)
                .forks(2)
                .verbosity(VerboseMode.NORMAL)
                .build();
        new Runner(options).run();
    }
}
