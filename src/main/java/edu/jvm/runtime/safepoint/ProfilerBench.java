package edu.jvm.runtime.safepoint;

import edu.Utils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.profile.LinuxPerfAsmProfiler;
import org.openjdk.jmh.profile.StackProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.VerboseMode;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@Fork(value = 2, jvmArgs = {
        "-XX:-UseBiasedLocking",
        "-Djmh.stack.lines=4",
        "-Djmh.stack.period=20"
//        "-XX:+UnlockDiagnosticVMOptions",
//        "-XX:+TraceClassLoading",
//        "-XX:+LogCompilation",
//        "-XX:+PrintAssembly",
//        "-XX:PrintAssemblyOptions=intel",
//        "-XX:CompileCommand=print,*ProfilerBench_cold.*hot*",
})
@Warmup(iterations = 2, time = 5, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 2, time = 10, timeUnit = TimeUnit.SECONDS)
public class ProfilerBench {

    public static final int ITERATIONS_COUNT = Integer.MAX_VALUE >> 5;

    public double hot() {
        return Utils.slowpoke(16);
    }

    @Benchmark
    public double coldIntLoop() {
        double result = 0;
        for (int i = 0; i < ITERATIONS_COUNT; i++) {
            result += hot();
        }
        return result;
    }

    @Benchmark
    public double coldLongLoop() {
        double result = 0;
        for (long i = 0; i < ITERATIONS_COUNT; i++) {
            result += hot();
        }
        return result;
    }

    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder()
                .include(ProfilerBench.class.getName())
                .addProfiler(StackProfiler.class)
//                .addProfiler(HotspotRuntimeProfiler.class)
//                .addProfiler(LinuxPerfAsmProfiler.class)
                .verbosity(VerboseMode.NORMAL)
                .build();
        new Runner(options).run();
    }
}
