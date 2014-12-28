package edu;

import java.io.File;

/**
 *
 */
public class NativeUtils {

    static {
        try {
            System.loadLibrary("util");
        } catch (UnsatisfiedLinkError e) {
            System.err.println("Native code library failed to load from: " +
                            System.getProperty("java.library.path") +
                            File.pathSeparator +
                            System.getProperty("user.dir")
            );
            e.printStackTrace();
            System.exit(1);
        }
    }

    public native static long cpuidrdtsc();

    public native static long rdtsc();

    public native static long rdtscp();
}
