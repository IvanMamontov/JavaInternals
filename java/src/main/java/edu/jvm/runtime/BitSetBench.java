package edu.jvm.runtime;

import org.apache.lucene.util.FixedBitSet;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.VerboseMode;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 2, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(value = 1, jvmArgs = {"-Xmx4G", "-Xms4G",
        "-XX:-UsePopCountInstruction",
        "-XX:-UseCountLeadingZerosInstruction",
        "-XX:-UseXMMForArrayCopy",
        "-XX:UseAVX=0"
})
@State(Scope.Benchmark)
public class BitSetBench {


    /*
     * This sample serves as a warning against subtle differences in cache access patterns.
     *
     * Many performance differences may be explained by the way tests are accessing memory.
     * In the example below, we walk the data either row-first, or col-first:
     */

    private final static int COUNT = 25;
    private FixedBitSet[] data;

    private Random random;

    @Param({"16777216"})
    private int size;

    @Param({"0.66", "0.33", "0.1"})
    private float probability;

    @Setup
    public void init() {
        random = new Random(0xDEAD_BEEF);
        data = new FixedBitSet[COUNT];
        for (int i = 0; i < data.length; i++) {
            FixedBitSet fixedBitSet = new FixedBitSet(size);
            for (int j = 0; j < size; j++) {
                if (random.nextFloat() < probability) {
                    fixedBitSet.set(j);
                }
            }
            data[i] = fixedBitSet;

        }

    }

    private int nextBitSet() {
        return random.nextInt(COUNT-1);
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public boolean intersects() {
        int first = nextBitSet();
        int second = nextBitSet();
        return data[first].intersects(data[second]);
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public int cardinality() {
        int first = nextBitSet();
        return data[first].cardinality();
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public int nextSetBit() {
        int first = nextBitSet();
        FixedBitSet fixedBitSet = data[first];
        int i = 0;
        while ((i = fixedBitSet.nextSetBit(i)) < size -1 ) {
            i++;
        }
        return i;
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public int prevSetBit() {
        int first = nextBitSet();
        FixedBitSet fixedBitSet = data[first];
        int i = size - 1;
        while ((i = fixedBitSet.prevSetBit(i)) > 1 ) {
            i--;
        }
        return i;
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public FixedBitSet clone() {
        int first = nextBitSet();
        FixedBitSet fixedBitSet = data[first];
        return fixedBitSet.clone();
    }


    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder()
                .include(BitSetBench.class.getName())
//                .addProfiler(LinuxPerfAsmProfiler.class)
//                .addProfiler(StackProfiler.class)
//                .addProfiler(LinuxPerfProfiler.class)
//                .addProfiler(GCProfiler.class)
                .verbosity(VerboseMode.NORMAL)
                .build();
        new Runner(options).run();
    }
}