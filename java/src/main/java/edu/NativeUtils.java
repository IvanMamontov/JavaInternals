package edu;

import java.io.File;

/**
 *
 */
public class NativeUtils {

    private NativeUtils() {
    }

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

    /**
     * RDTSCP - Read Time-Stamp Counter and Processor ID IA assembly instruction.
     * The RDTSCP instruction is described in the Intel® 64 and IA-32 Architectures
     * Software Developer’s Manual Volume 2B ([3]) as an assembly instruction that, at
     * the same time, reads the timestamp register and the CPU identifier. The value of
     * the timestamp register is stored into the EDX and EAX registers; the value of the
     * CPU id is stored into the ECX register (“On processors that support the Intel 64
     * architecture, the high order 32 bits of each of RAX, RDX, and RCX are cleared”).
     * What is interesting in this case is the “pseudo” serializing property of RDTSCP. The
     * manual states:
     * “The RDTSCP instruction waits until all previous instructions have been executed
     * before reading the counter. However, subsequent instructions may begin execution
     * before the read operation is performed.”
     * This means that this instruction guarantees that everything that is above its call in
     * the source code is executed before the instruction itself is called. It cannot,
     * however, guarantee that ¾ for optimization purposes ¾ the CPU will not execute,
     * before the RDTSCP call, instructions that, in the source code, are placed after the
     * RDTSCP function call itself. If this happens, a contamination caused by instructions
     * in the source code that come after the RDTSCP will occur in the code under
     * measurement.
     *
     * @see <a href="http://www.intel.com/content/dam/www/public/us/en/documents/white-papers/ia-32-ia-64-benchmark-code-execution-paper.pdf">How to Benchmark
     * Code Execution</a>
     * @see <a href="https://www.ccsl.carleton.ca/~jamuir/rdtscpm1.pdf">Using the RDTSC Instruction for Performance Monitoring</a>
     */
    public native static long rdtscp();
}
