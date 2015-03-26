package edu.jvm.runtime;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.profile.LinuxPerfAsmProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 3, time = 5, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3, time = 5, timeUnit = TimeUnit.SECONDS)
@Fork(value = 1)
public class RoundBench {

    float[] floats;
    int i;

    @Setup
    public void initI() {
        Random random = new Random(0xDEAD_BEEF);
        floats = new float[8096];
        for (int i = 0; i < floats.length; i++) {
            floats[i] = random.nextFloat();
        }
    }

    @Benchmark
    public int baseline() {
        i++;
        i = i & 0xFFFFFF00;
        return i;
    }

    @Benchmark
    public int round() {
        i++;
        i = i & 0xFFFFFF00;
        return Math.round(floats[i]);
    }

    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder()
                .include(RoundBench.class.getName())
                .addProfiler(LinuxPerfAsmProfiler.class)
                .build();
        new Runner(options).run();
    }
}