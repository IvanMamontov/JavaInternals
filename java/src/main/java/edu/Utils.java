package edu;

import java.util.Random;

/**
 *
 */
public class Utils {

    public static final int SEED = 0xCAFEBABE;

    public static double[] newRandomDoubleArray(int size) {
        double[] arr = new double[size];
        fill(arr);
        return arr;
    }

    public static int[] newRandomIntArray(int size) {
        int[] arr = new int[size];
        fill(arr);
        return arr;
    }

    private static void fill(int[] array) {
        Random rnd = new Random(SEED);
        for (int i = 0; i < array.length; i++) {
            array[i] = rnd.nextInt();
        }
    }

    private static void fill(double[] array) {
        Random rnd = new Random(SEED);
        for (int i = 0; i < array.length; i++) {
            array[i] = rnd.nextDouble();
        }
    }

    /**
     * Hotspot doesn't put safepoints into counted int loops, because it
     * assumes that they will terminate just "fast enough"(In this case
     * server compiler will generate more optimal loop code).
     * Even a stop-the-world will have to wait until this loop will finish.
     * <p/>
     * In this method we have very tight loop which do small but expensive
     * computations without safepoint polling.
     * <p/>
     * <b>Note:</b> this method has non-linear growth on low iteration counts.
     *
     * @param iterations amount of loop iterations.
     * @see <a href="https://bugs.openjdk.java.net/browse/JDK-6869327">Add new
     * C2 flag to keep safepoints in counted loops</a>
     */
    public static double slowpoke(int iterations) {
        double d = 0;
        for (int j = 1; j < iterations; j++) {
            d += Math.log(Math.E * j);
        }
        return d;
    }
}
