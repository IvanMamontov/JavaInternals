package edu;

import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

/**
 * The server compiler likes a loop with an int counter (int i = 0),
 * a constant stride (i++), and loop-invariant limit (i <= n).
 *
 * @see <a href="https://wikis.oracle.com/display/HotSpotInternals/PerformanceTechniques">Performance Techniques</a>
 */
@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 5, timeUnit = TimeUnit.SECONDS)
@Fork(value = 5)
public class LoopBack {

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

    public static int foreachSum(int[] arr) {
        int s = 0;
        for (int x : arr) {
            s += x;
        }
        return s;
    }

    public static int forwardSum(int[] arr) {
        int s = 0;
        for (int i = 0; i < arr.length; i++) {
            s += arr[i];
        }
        return s;
    }

    public static int backwardSum(int[] arr) {
        int s = 0;
        for (int i = arr.length - 1; i >= 0; i--) {
            s += arr[i];
        }
        return s;
    }

    @Benchmark
    @OperationsPerInvocation(K)
    @BenchmarkMode({Mode.AverageTime})
    public int foreach_1K() {
        return foreachSum(arr1K);
    }

    @Benchmark
    @OperationsPerInvocation(K)
    @BenchmarkMode({Mode.AverageTime})
    public int forward_1K() {
        return forwardSum(arr1K);
    }

    @Benchmark
    @OperationsPerInvocation(K)
    @BenchmarkMode({Mode.AverageTime})
    public int backward_1K() {
        return backwardSum(arr1K);
    }

    @Benchmark
    @OperationsPerInvocation(32 * K)
    @BenchmarkMode({Mode.AverageTime})
    public int foreach_32K() {
        return foreachSum(arr32K);
    }

    @Benchmark
    @OperationsPerInvocation(32 * K)
    @BenchmarkMode({Mode.AverageTime})
    public int forward_32K() {
        return forwardSum(arr32K);
    }

    @Benchmark
    @OperationsPerInvocation(32 * K)
    @BenchmarkMode({Mode.AverageTime})
    public int backward_32K() {
        return backwardSum(arr32K);
    }

    @Benchmark
    @OperationsPerInvocation(4 * M)
    @BenchmarkMode({Mode.AverageTime})
    public int foreach_4M() {
        return foreachSum(arr4M);
    }

    @Benchmark
    @OperationsPerInvocation(4 * M)
    @BenchmarkMode({Mode.AverageTime})
    public int forward_4M() {
        return forwardSum(arr4M);
    }

    @Benchmark
    @OperationsPerInvocation(4 * M)
    @BenchmarkMode({Mode.AverageTime})
    public int backward_4M() {
        return backwardSum(arr4M);
    }
}