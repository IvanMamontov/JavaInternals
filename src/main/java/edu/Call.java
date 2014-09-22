package edu;

import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

/**
 *
 * get and try to explain performance results
 */
@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@CompilerControl(CompilerControl.Mode.DONT_INLINE)
@Fork(value = 3)
@Warmup(iterations = 1, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 2, time = 2, timeUnit = TimeUnit.SECONDS)
public class Call {

    public static int foo00() {
        return 1;
    }

    @Benchmark
    @OperationsPerInvocation(1)
    public static int foo01() {
        return foo00();
    }

    @Benchmark
    @OperationsPerInvocation(2)
    public static int foo02() {
        return foo01();
    }

    @Benchmark
    @OperationsPerInvocation(3)
    public static int foo03() {
        return foo02();
    }

    @Benchmark
    @OperationsPerInvocation(4)
    public static int foo04() {
        return foo03();
    }

    @Benchmark
    @OperationsPerInvocation(5)
    public static int foo05() {
        return foo04();
    }

    @Benchmark
    @OperationsPerInvocation(6)
    public static int foo06() {
        return foo05();
    }

    @Benchmark
    @OperationsPerInvocation(7)
    public static int foo07() {
        return foo06();
    }

    @Benchmark
    @OperationsPerInvocation(8)
    public static int foo08() {
        return foo07();
    }

    @Benchmark
    @OperationsPerInvocation(9)
    public static int foo09() {
        return foo08();
    }

    @Benchmark
    @OperationsPerInvocation(10)
    public static int foo10() {
        return foo09();
    }

    @Benchmark
    @OperationsPerInvocation(11)
    public static int foo11() {
        return foo10();
    }

    @Benchmark
    @OperationsPerInvocation(12)
    public static int foo12() {
        return foo11();
    }

    @Benchmark
    @OperationsPerInvocation(13)
    public static int foo13() {
        return foo12();
    }

    @Benchmark
    @OperationsPerInvocation(14)
    public static int foo14() {
        return foo13();
    }

    @Benchmark
    @OperationsPerInvocation(15)
    public static int foo15() {
        return foo14();
    }

    @Benchmark
    @OperationsPerInvocation(16)
    public static int foo16() {
        return foo15();
    }

    @Benchmark
    @OperationsPerInvocation(17)
    public static int foo17() {
        return foo16();
    }

    @Benchmark
    @OperationsPerInvocation(18)
    public static int foo18() {
        return foo17();
    }

    @Benchmark
    @OperationsPerInvocation(19)
    public static int foo19() {
        return foo18();
    }

    @Benchmark
    @OperationsPerInvocation(20)
    public static int foo20() {
        return foo19();
    }


}
