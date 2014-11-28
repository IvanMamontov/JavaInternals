package edu.jvm.runtime.natives;

import edu.SysTime;
import me.lemire.integercompression.FastPFOR128;
import me.lemire.integercompression.IntWrapper;
import org.openjdk.jmh.annotations.*;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 1, time = 1000, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 1, time = 1000, timeUnit = TimeUnit.MILLISECONDS)
@Fork(value = 1, jvmArgs = {"-Djava.library.path=/Users/imamontov/projects/JMH_examples/native/macosx/target/"})
@State(Scope.Thread)
public class CodecsBench {

    @Param({"256", "4096", "8192", "16384" , "65536"})
    int length;

    private int[] docIdSet;
    private byte[] bufferByte;
    private byte[] bufferByte2;
    private int[] bufferInt;

    @Setup
    public void setup(){
        Random random = new Random(0xDEADBEEF);
        docIdSet = new int[length];
        bufferByte = new byte[length];
        bufferByte2 = new byte[length];
        bufferInt = new int[length];
        int lastValue = 1;
        for (int i = 0; i < docIdSet.length; i++) {
            lastValue = lastValue + random.nextInt(10) + 1;
            docIdSet[i] = lastValue;
        }
    }

    @Benchmark
    public int latency_compress_native() {
        return SysTime.compress(docIdSet, bufferByte);
    }

    @Benchmark
    public int latency_compress_native_critical() {
        return SysTime.compressCritical(docIdSet, bufferByte2);
    }

    @Benchmark
    public int latency_compress_java() {
        FastPFOR128 codec = new FastPFOR128();
        IntWrapper inputoffset = new IntWrapper(0);
        IntWrapper outputoffset = new IntWrapper(0);
        codec.compress(docIdSet, inputoffset, length, bufferInt, outputoffset);
        return outputoffset.get();
    }
}
