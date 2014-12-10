package edu;

/**
 *
 */
public class SysTime {

    static {
        try {
            System.loadLibrary("util");
        } catch (UnsatisfiedLinkError e) {
            System.err.println("Native code library failed to load: " + System.getProperty("java.library.path") + e);
            System.exit(1);
        }
    }

    public native static long cpuidrdtsc();

    public native static long rdtsc();

    public native static long rdtscp();
}
