package edu.jvm.runtime.natives;

import edu.Utils;
import org.openjdk.jmh.annotations.*;

@State(Scope.Benchmark)
@Fork(value = 2,
        jvmArgs = {
                "-Djava.library.path=./target"
        })
public class Natives {

    @Param({"16", "256", "4096", "65536", "1048576"})
    int length;
    byte[] array;

    @Setup
    public void setup() {
        array = new byte[length];
    }

    @Benchmark
    public int arrayRegion() {
        System.out.println(System.getProperty("java.library.path"));
        return Utils.arrayRegionImpl(array);
    }

//    @Benchmark
//    public int arrayElements() {
//        return Utils.arrayElementsImpl(array);
//    }
//
//    @Benchmark
//    public int arrayElementsCritical() {
//        return Utils.arrayElementsCriticalImpl(array);
//    }
//
//    @Benchmark
//    public int javaCritical() {
//        return Utils.javaCriticalImpl(array);
//    }

}