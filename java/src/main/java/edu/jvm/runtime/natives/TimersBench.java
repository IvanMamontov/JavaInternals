package edu.jvm.runtime.natives;

import edu.SysTime;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.VerboseMode;

import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1000, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 5, time = 1000, timeUnit = TimeUnit.MILLISECONDS)
@Fork(value = 5)
@State(Scope.Thread)
public class TimersBench {

    private long lastValue;

    @Benchmark
    public long latency_nanotime() {
        return System.nanoTime();
    }

//    @Benchmark
//    public long latency_currentTime() {
//        return System.currentTimeMillis();
//    }

    @Benchmark
    public long latency_rdtsc() {
        return SysTime.rdtsc();
    }

    @Benchmark
    public long latency_baseline() {
        return lastValue++;
    }

    @Benchmark
    public long granularity_nanotime() {
        long cur;
        do {
            cur = System.nanoTime();
        } while (cur == lastValue);
        lastValue = cur;
        return cur;
    }

//    @Benchmark
//    public long granularity_currentTime() {
//        long cur;
//        do {
//            cur = System.currentTimeMillis();
//        } while (cur == lastValue);
//        lastValue = cur;
//        return cur;
//    }

    @Benchmark
    public long granularity_rdtsc() {
        long cur;
        do {
            cur = SysTime.rdtsc();
        } while (cur == lastValue);
        lastValue = cur;
        return cur;
    }

    @Benchmark
    public long granularity_baseline() {
        long cur;
        do {
            cur = lastValue++;
        } while (cur == lastValue);
        lastValue = cur;
        return cur;
    }

    public static void main(String[] args) throws RunnerException, InterruptedException {
        PrintWriter pw = new PrintWriter(System.out, true);

        pw.println("---- 8< (cut here) -----------------------------------------");

        pw.println(System.getProperty("java.runtime.name") + ", " + System.getProperty("java.runtime.version"));
        pw.println(System.getProperty("java.vm.name") + ", " + System.getProperty("java.vm.version"));
        pw.println(System.getProperty("os.name") + ", " + System.getProperty("os.version") + ", " + System.getProperty("os.arch"));

        int cpus = figureOutHotCPUs(pw);
//
//        runWith(pw, 1,          "-client");
//        runWith(pw, cpus / 2,   "-client");
//        runWith(pw, cpus,       "-client");

        runWith(pw, 1,          "-server");
        runWith(pw, cpus / 2,   "-server");
        runWith(pw, cpus,       "-server");

        pw.println();
        pw.println("---- 8< (cut here) -----------------------------------------");

        pw.flush();
        pw.close();
    }

    private static void runWith(PrintWriter pw, int threads, String... jvmOpts) throws RunnerException {
        pw.println();
        pw.println("Running with " + threads + " threads and " + Arrays.toString(jvmOpts) + ": ");

        Options opts = new OptionsBuilder()
                .threads(threads)
                .verbosity(VerboseMode.SILENT)
                .jvmArgs(jvmOpts)
                .build();

        Collection<RunResult> results = new Runner(opts).run();
        for (RunResult r : results) {
            String benchmark = r.getParams().getBenchmark();
            String name = benchmark.substring(benchmark.lastIndexOf(".") + 1);
            double score = r.getPrimaryResult().getScore();
            double scoreError = r.getPrimaryResult().getStatistics().getMeanErrorAt(0.99);
            pw.printf("%30s: %11.3f +- %10.3f ns%n", name, score, scoreError);
        }
    }


    /**
     * Warm up the CPU schedulers, bring all the CPUs online to get the
     * reasonable estimate of the system capacity.
     *
     * @return online CPU count
     */
    private static int figureOutHotCPUs(PrintWriter pw) throws InterruptedException {
        ExecutorService service = Executors.newCachedThreadPool();

        pw.println();
        pw.print("Burning up to figure out the exact CPU count...");
        pw.flush();

        int warmupTime = 1000;
        long lastChange = System.currentTimeMillis();

        List<Future<?>> futures = new ArrayList<Future<?>>();
        futures.add(service.submit(new BurningTask()));

        pw.print(".");

        int max = 0;
        while (System.currentTimeMillis() - lastChange < warmupTime) {
            int cur = Runtime.getRuntime().availableProcessors();
            if (cur > max) {
                pw.print(".");
                max = cur;
                lastChange = System.currentTimeMillis();
                futures.add(service.submit(new BurningTask()));
            }
        }

        for (Future<?> f : futures) {
            pw.print(".");
            f.cancel(true);
        }

        service.shutdown();

        service.awaitTermination(1, TimeUnit.DAYS);

        pw.println(" done!");

        return max;
    }

    public static class BurningTask implements Runnable {
        @Override
        public void run() {
            while (!Thread.interrupted()); // burn;
        }
    }

}
