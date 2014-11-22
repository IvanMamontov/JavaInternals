package edu;

import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 2, time = 10, timeUnit = TimeUnit.SECONDS)
@Fork(value = 2)
public class BadValue {

    public static final int SIZE = 1024;

    public double[] src;
    public double[] dst;

    public double VALUE1 = 0.01;
    public double VALUE2 = Double.MIN_NORMAL - Double.MIN_VALUE;

    @Setup
    public void init() {
        src = Utils.newRandomDoubleArray(SIZE);
        dst = new double[SIZE];
    }

    @Benchmark
    @OperationsPerInvocation(SIZE)
    public void testAddNormalValue() {
        for (int i = 0; i < src.length; i++) {
            dst[i] = src[i] + VALUE1;
        }
    }

    @Benchmark
    @OperationsPerInvocation(SIZE)
    public void testAddBadValue() {
        for (int i = 0; i < src.length; i++) {
            dst[i] = src[i] + VALUE2;
        }
    }

}