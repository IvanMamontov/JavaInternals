package edu.heap;

import com.google.caliper.Runner;
import com.google.caliper.SimpleBenchmark;

/**
 * To start use following command:
 * {@code java edu.heap.HeapWalkBenchmark -JmemoryMax=-Xmx4g -JmemoryStart=-Xms4g --trials 1}
 */
public class HeapWalkBenchmark extends SimpleBenchmark {

    public void timeLinear(int reps) {
        LinearMemoryWalker linearMemoryWalker = new LinearMemoryWalker();
        for (int i = 0; i < reps; i++) {
            linearMemoryWalker.doWalk();
        }
    }

    public void timeRandomPage(int reps) {
        RandomPageWalker randomPageWalker = new RandomPageWalker();
        for (int i = 0; i < reps; i++) {
            randomPageWalker.doWalk();
        }
    }

    public void timeRandomHeap(int reps) {
        RandomHeapWalker randomHeapWalker = new RandomHeapWalker();
        for (int i = 0; i < reps; i++) {
            randomHeapWalker.doWalk();
        }
    }


    // TODO: remove this from all examples when IDE plugins are ready
    public static void main(String[] args) throws Exception {
        Runner.main(HeapWalkBenchmark.class, args);
    }
}