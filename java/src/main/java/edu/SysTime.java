package edu;

//
// Definition of native methods to get various clock values from Java
//
public class SysTime
{

    static {
        try {
            System.load("/Users/imamontov/projects/JMH_examples/native/macosx/target/libutil.jnilib");
        } catch (UnsatisfiedLinkError e) {
            System.err.println("Native code library failed to load.\n" + e);
            System.exit(1);
        }
    }

//    public native static long clocktime();
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
//            long sysTime = SysTime.clocktime();

            System.out.println();
            System.out.printf("System.millis          =%,d\n",msNow);
            System.out.printf("System.nanoTime        =%,d\n",nsNow);
//            System.out.printf("SysTime.clocktime      =%,d\n",sysTime);
            System.out.printf("SysTime.cpuid_rdtsc    =%,d\n",cpuid_tsc);
            System.out.printf("SysTime.rdtsc          =%,d\n",tsc);
            System.out.printf("SysTime.rdtscp         =%,d\n",tscp);
        }
    }
}
