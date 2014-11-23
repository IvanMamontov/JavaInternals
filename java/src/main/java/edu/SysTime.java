package edu;

/**
 *
 */
public class SysTime
{

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


    public static void main(String[] args)
    {
        int count = (0 < args.length)? Integer.parseInt(args[0]): 100000;
        for (int i = count; 0 <= --i; ) {
            long msNow = System.currentTimeMillis();
            long nsNow = System.nanoTime();
            long tsc = SysTime.rdtsc();
            long tscp = SysTime.rdtscp();
            long cpuid_tsc = SysTime.cpuidrdtsc();

            System.out.println();
            System.out.printf("System.millis          =%,d\n",msNow);
            System.out.printf("System.nanoTime        =%,d\n",nsNow);
            System.out.printf("SysTime.cpuid_rdtsc    =%,d\n",cpuid_tsc);
            System.out.printf("SysTime.rdtsc          =%,d\n",tsc);
            System.out.printf("SysTime.rdtscp         =%,d\n",tscp);
        }
    }
}
