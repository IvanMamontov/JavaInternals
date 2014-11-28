package edu;

import java.util.Random;

/**
 *
 */
public class SysTime {

    static {
        try {
            System.loadLibrary("util");
        } catch (UnsatisfiedLinkError e) {
            System.err.println("Native code library failed to load.\n" + e);
            System.exit(1);
        }
    }

    public native static long cpuidrdtsc();

    public native static long rdtsc();

    public native static long rdtscp();

    /**
     * Compresses data from datain to buffer, returns how many bytes written.
     */
    public native static int compress(int[] datain, byte[] buffer);

    /**
     * Compresses data from datain to buffer, returns how many bytes written.
     */
    public native static int compressCritical(int[] datain, byte[] buffer);

    public static void main(String[] args) {
        int[] datain = new int[128 * 10000];
        Random random = new Random();
        int value = 0;
        for (int i = 0; i < datain.length; i++) {
            value = value + 1 + random.nextInt(3);
            datain[i] = value;
        }
        int compress1 = compress(datain, new byte[128 * 10000]);
        int compress2 = compressCritical(datain, new byte[128 * 10000]);
        System.out.println("compress1 = " + compress1);
        System.out.println("compress2 = " + compress2);

        long msNow = System.currentTimeMillis();
        long nsNow = System.nanoTime();
        long tsc = SysTime.rdtsc();
        long tscp = SysTime.rdtscp();
        long cpuid_tsc = SysTime.cpuidrdtsc();

        System.out.println();
        System.out.printf("System.millis          =%,d\n", msNow);
        System.out.printf("System.nanoTime        =%,d\n", nsNow);
        System.out.printf("SysTime.cpuid_rdtsc    =%,d\n", cpuid_tsc);
        System.out.printf("SysTime.rdtsc          =%,d\n", tsc);
        System.out.printf("SysTime.rdtscp         =%,d\n", tscp);
    }
}
