package org.openjdk.jcstress.tests.volatiles;

import edu.Utils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.profile.LinuxPerfAsmProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.VerboseMode;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 2, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 2, time = 2, timeUnit = TimeUnit.SECONDS)
@Fork(value = 1)
public class ReadAfterReadTest {

    private static final int K = 1024;

    private int[] arr1K;


    private final Holder h1 = new Holder();
    private final Holder h2 = h1;

    private static class Holder {
        int a;
        int trap;
    }


    @Setup
    public void initI() {
        arr1K = Utils.newRandomIntArray(K);
    }

    @Benchmark
    @BenchmarkMode({Mode.AverageTime})
    public void actor2(Blackhole bh) {
        Holder h1 = this.h1;
        Holder h2 = this.h2;

        // Spam null-pointer check folding: try to step on NPEs early.
        // Doing this early frees compiler from moving h1.a and h2.a loads
        // around, because it would not have to maintain exception order anymore.
        h1.trap = 0;
        h2.trap = 0;

        // Spam alias analysis: the code effectively reads the same field twice,
        // but compiler does not know (h1 == h2) (i.e. does not check it, as
        // this is not a profitable opt for real code), so it issues two independent
        // loads.
        bh.consume(h1.a);
        bh.consume(h2.a);
    }


    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder()
                .include(ReadAfterReadTest.class.getName())
                .addProfiler(LinuxPerfAsmProfiler.class)
//                .addProfiler(StackProfiler.class)
//                .addProfiler(LinuxPerfProfiler.class)
//                .addProfiler(GCProfiler.class)
                .verbosity(VerboseMode.NORMAL)
                .build();
        new Runner(options).run();
    }
}