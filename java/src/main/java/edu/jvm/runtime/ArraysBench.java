package edu.jvm.runtime;

import edu.Utils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.profile.LinuxPerfAsmProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.VerboseMode;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 2, time = 5, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3, time = 5, timeUnit = TimeUnit.SECONDS)
@Fork(value = 2, jvmArgs = {
//        "-XX:+UnlockDiagnosticVMOptions",
//        "-XX:+PrintInlining",
//        "-XX:+DebugNonSafepoints",
//        "-XX:+UnlockCommercialFeatures",
//        "-XX:+FlightRecorder",
//        "-XX:StartFlightRecording=duration=60s,settings=profile,filename=/tmp/myrecording.jfr"
})
@State(Scope.Benchmark)
public class ArraysBench {

    public static final int K = 1024;
    public static final int M = K * K;

    private int[] arr1K;
    private int[] arr32K;
    private int[] arr4M;

    @Setup
    public void initI() {
        arr1K = Utils.newRandomIntArray(K);
        arr32K = Utils.newRandomIntArray(32 * K);
        arr4M = Utils.newRandomIntArray(4 * M);
    }

    @Benchmark
    public void calcArray() {
        System.arraycopy(arr4M, 0, arr4M, 0, 500000);
    }

    @Benchmark
    public void calcArrayOther() {
        System.arraycopy(arr4M, 0, arr4M, 0, 0);
    }

    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder()
                .include(ArraysBench.class.getName())
                .addProfiler(LinuxPerfAsmProfiler.class)
//                .addProfiler(StackProfiler.class)
//                .addProfiler(LinuxPerfProfiler.class)
//                .addProfiler(GCProfiler.class)
                .verbosity(VerboseMode.NORMAL)
                .build();
        new Runner(options).run();
    }
}