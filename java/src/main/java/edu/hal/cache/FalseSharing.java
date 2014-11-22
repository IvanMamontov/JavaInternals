package edu.hal.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLongArray;

/**
 * @author Ivan Mamontov
 */
public class FalseSharing {

    private static final AtomicLongArray array = new AtomicLongArray(4 * 1024);

    public static void main(String[] args) throws Exception {
        int[] values = new int[]{0, 0, 0, 1, 2, 3, 4, 5, 6, 7, 8, 1000};
        for (int i = 0; i < values.length; i++) {
            values[i] = i;
        }
        for (int threads = 1; threads <= 8; threads++) {
            for (int sepIndex : values) {
                System.out.println("" + threads + " " + sepIndex + " " + runRound(threads, sepIndex));
            }
        }
    }

    private static long runRound(int threads, final int sepIndex) throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(threads);
        Collection<Callable<Object>> tasks = new ArrayList<Callable<Object>>(threads);
        for (int i = 0; i < threads; i++) {
            final int threadIndex = i;
            tasks.add(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    int currentIndex = threadIndex * sepIndex;
                    long value = (long) Math.log(array.get(currentIndex));
                    array.set(currentIndex, value);
                    return null;
                }
            });
        }
        long start = System.currentTimeMillis();
        List<Future<Object>> futures = executorService.invokeAll(tasks);
        for (Future<Object> future : futures) {
            future.get();
        }
        return System.currentTimeMillis() - start;
    }

}
