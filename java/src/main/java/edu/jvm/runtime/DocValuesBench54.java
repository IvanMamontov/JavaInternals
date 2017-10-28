package edu.jvm.runtime;

import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.FixedBitSet;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.VerboseMode;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 1, time = 2, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3, time = 3, timeUnit = TimeUnit.SECONDS)
@Fork(value = 1, jvmArgs = {
//        "-XX:+UnlockDiagnosticVMOptions",
//        "-XX:+PrintInlining",
//        "-XX:+DebugNonSafepoints",
//        "-XX:+UnlockCommercialFeatures",
//        "-XX:+FlightRecorder",
//        "-XX:StartFlightRecording=duration=60s,settings=profile,filename=/tmp/myrecording.jfr"
})
@State(Scope.Benchmark)
public class DocValuesBench54 {

    private static final String INDEX_PATH = "/home/imamontov/Downloads/products/restore.20170817052816863";
    private LeafReaderContext leafReaderContext;
    private DirectoryReader reader;

    @Param({"65", "99", "44", "45"})
    private int store;

    private FixedBitSet bitSet;
    private IndexSearcher searcher;
    private int maxDoc;
    private Random random;

    @Setup
    public void init() throws IOException {
        random = new Random(0xDEAD_BEEF);
        FSDirectory directory = FSDirectory.open(new File(INDEX_PATH).toPath());
        reader = DirectoryReader.open(directory);
        searcher = new IndexSearcher(reader);
        List<LeafReaderContext> leaves = reader.leaves();
        leafReaderContext = leaves.get(0);
        maxDoc = reader.numDocs();

        //this bitset is used as baseline for all measurements
        bitSet = new FixedBitSet(maxDoc);
        Query query = new DocValuesNumbersQuery2(getStore(), 1L);
        query = query.rewrite(reader);
        Weight weight = query.createWeight(searcher, false, 0);
        Scorer scorer = weight.scorer(leafReaderContext);
        DocIdSetIterator docs = scorer.iterator();
        int doc;
        while ((doc = docs.nextDoc()) != DocIdSetIterator.NO_MORE_DOCS) {
            bitSet.set(doc);
        }
    }

    private String getStore() {
        return "store_" + store;
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public boolean randomGetBitSet() throws IOException {
        return bitSet.get(random.nextInt(maxDoc - 1));
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public long randomGetDocValues() throws IOException {
        NumericDocValues numericDocValues = DocValues.getNumeric(leafReaderContext.reader(), getStore());
        return numericDocValues.advance(random.nextInt(maxDoc - 1));
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public int iterateAllBitSet() throws IOException {
        int result = 0;
        for (int ord = bitSet.nextSetBit(0); ord != DocIdSetIterator.NO_MORE_DOCS; ord = ord + 1 >= bitSet.length() ? DocIdSetIterator.NO_MORE_DOCS : bitSet.nextSetBit(ord + 1)) {
            result++;
        }
        return result;
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public int iterateAllRangeQuery() throws IOException {
        int result;
        Query query = new DocValuesNumbersQuery2(getStore(), 1L);
        query = query.rewrite(reader);
        Weight weight = query.createWeight(searcher, false, 0);
        Scorer scorer = weight.scorer(leafReaderContext);
        DocIdSetIterator docs = scorer.iterator();
        result = 0;
        while (docs.nextDoc() != DocIdSetIterator.NO_MORE_DOCS) {
            result++;
        }
        return result;
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public int iterateAllNumberQuery() throws IOException {
        int result;
        Query query = new DocValuesNumbersQuery2(getStore(), 1L);
        query = query.rewrite(reader);
        Weight weight = query.createWeight(searcher, false, 0);
        Scorer scorer = weight.scorer(leafReaderContext);
        DocIdSetIterator docs = scorer.iterator();
        result = 0;
        while (docs.nextDoc() != DocIdSetIterator.NO_MORE_DOCS) {
            result++;
        }
        return result;
    }


    @Benchmark
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public int iterateAllIvansQuery() throws IOException {
        int result;
        Query query = new DocValuesIvanQuery(getStore(), 1L);
        query = query.rewrite(reader);
        Weight weight = query.createWeight(searcher, false, 0);
        Scorer scorer = weight.scorer(leafReaderContext);
        DocIdSetIterator docs = scorer.iterator();
        result = 0;
        while (docs.nextDoc() != DocIdSetIterator.NO_MORE_DOCS) {
            result++;
        }
        return result;
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public int iterateAllDocValues() throws IOException {
        int result = 0;
        NumericDocValues numericDocValues = DocValues.getNumeric(leafReaderContext.reader(), getStore());
        for (int j = 0; j < maxDoc; j++) {
            long value = numericDocValues.advance(j);
            result += value;
        }
        return result;
    }

    public static void main(String[] args) throws Exception {

        Options options = new OptionsBuilder()
                .include(DocValuesBench54.class.getName())
//                .addProfiler(LinuxPerfAsmProfiler.class)
//                .addProfiler(StackProfiler.class)
//                .addProfiler(LinuxPerfProfiler.class)
//                .addProfiler(GCProfiler.class)
                .verbosity(VerboseMode.NORMAL)
                .build();
        new Runner(options).run();
    }
}