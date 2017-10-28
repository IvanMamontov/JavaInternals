package edu.jvm.runtime;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.NumericDocValuesField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.search.DocValuesNumbersQuery2;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.FixedBitSet;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.profile.LinuxPerfAsmProfiler;
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
@Warmup(iterations = 3, time = 5, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 2, time = 4, timeUnit = TimeUnit.SECONDS)
@Fork(value = 1, jvmArgs = {
//        "-XX:MaxInlineLevel=12",
//        "-XX:+UnlockDiagnosticVMOptions",
//        "-XX:+PrintInlining",
//        "-XX:+LogCompilation",
//        "-XX:LogFile=out.log"
//        "-XX:+DebugNonSafepoints",
//        "-XX:+UnlockCommercialFeatures",
//        "-XX:+FlightRecorder",
//        "-XX:StartFlightRecording=duration=60s,settings=profile,filename=/tmp/myrecording.jfr"
})
@State(Scope.Benchmark)
public class SynteticDocValuesBench54 {

    private static final String INDEX_PATH = "/home/imamontov/Downloads/products/restore.20170817052816863";
    private static final String TEMPORARY_PATH = "/tmp/bench";
    public static final String STORE_AS_IS = "store_";
    public static final String STORE_SPARSE = "sparce_store_";
    public static final String STORE_ONE_VAL = "one_val_store_";
    private LeafReaderContext leafReaderContext;
    private DirectoryReader reader;
    private IndexSearcher searcher;

//    @Param({"10", "35", "50", "60", "90"})
    @Param({"50"})
    private int store;

//    @Param({STORE_AS_IS, STORE_SPARSE, STORE_ONE_VAL})
    @Param({STORE_AS_IS})
    private String name;

    private FixedBitSet bitSet;
    private int maxDoc;

    @Setup
    public void init() throws IOException {
        copyColumn();
        FSDirectory directory = FSDirectory.open(new File(TEMPORARY_PATH).toPath());
        reader = DirectoryReader.open(directory);
        searcher = new IndexSearcher(reader);
        List<LeafReaderContext> leaves = reader.leaves();
        leafReaderContext = leaves.get(0);
        maxDoc = reader.numDocs();

        System.out.printf("evalRangeQueryResult %s %d%n", getStore(name), evalRangeQueryResult(getResultSet(name)));
        System.out.printf("evalRangeQueryResult2 %s %d%n", getStore(name), evalRangeQueryResult(getResultSet2(name)));
        System.out.printf("evalDocValues %s %d%n", getStore(name), evalDocValues(name));
    }

    /**
     * Creates a small copy of original index with column variations.
     */
    private void copyColumn() throws IOException {
        Random random = new Random();
//        FSDirectory originalIndexDirectory = FSDirectory.open(new File(INDEX_PATH).toPath());
//        DirectoryReader originalReader = DirectoryReader.open(originalIndexDirectory);
//        IndexSearcher originalSearcher = new IndexSearcher(originalReader);
//        List<LeafReaderContext> leaves = originalReader.leaves();
//        LeafReaderContext leafReaderContext = leaves.get(0);


        FSDirectory directory = FSDirectory.open(new File(TEMPORARY_PATH).toPath());
        IndexWriterConfig conf = new IndexWriterConfig(null);
        conf.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        conf.setUseCompoundFile(false);
        conf.setMaxBufferedDocs(1000000);
        conf.setRAMBufferSizeMB(2048);
        IndexWriter writer = new IndexWriter(directory, conf);

//        Query query = new DocValuesNumbersQuery2(getStore(STORE_AS_IS), 1L);
//        query = query.rewrite(originalReader);
//        Weight weight = query.createWeight(originalSearcher, false, 0);
//        Scorer scorer = weight.scorer(leafReaderContext);
//        DocIdSetIterator docs = scorer.iterator();
//        int document;
//
        int maxDoc = 5_000_000;
        bitSet = new FixedBitSet(maxDoc);
        float probability = store / 100f;
        for (int i = 0; i < maxDoc; i++) {
            if (random.nextFloat() < probability) {
                bitSet.set(i);
            }
        }

        for (int i = 0; i < maxDoc; i++) {

            Document doc = new Document();
            doc.add(new StringField("id", "" + i, Field.Store.NO));

            int value = bitSet.get(i) ? 1 : 0;
            if (value == 1) {
                doc.add(new NumericDocValuesField(STORE_ONE_VAL + store, 1));
                doc.add(new NumericDocValuesField(STORE_SPARSE + store, random.nextFloat() > 0.90f ? 0 : 1));
            }
            doc.add(new NumericDocValuesField(STORE_AS_IS + store, value));

            writer.addDocument(doc);
        }
        writer.forceMerge(1);
        writer.commit();

    }

    private String getStore(String storePrefix) {
        return storePrefix + store;
    }

    private int evalRangeQueryResult(DocIdSetIterator iterator) throws IOException {
        int result = 0;
        while (iterator.nextDoc() != DocIdSetIterator.NO_MORE_DOCS) {
            result++;
        }
        return result;
    }

    private int evalFastResult(String columnType) throws IOException {
        int result = 0;
        DocIdSetIterator docs = getResultSet(columnType);
        while (docs.nextDoc() != DocIdSetIterator.NO_MORE_DOCS) {
            result++;
        }
        return result;
    }

    private int evalDocValues(String columnType) throws IOException {
        int result = 0;
        NumericDocValues numericDocValues = DocValues.getNumeric(leafReaderContext.reader(), getStore(columnType));
        for (int ord = numericDocValues.advance(0); ord != DocIdSetIterator.NO_MORE_DOCS; ord = ord + 1 >= maxDoc ? DocIdSetIterator.NO_MORE_DOCS : numericDocValues.advance(ord + 1)) {
            if (numericDocValues.longValue() == 1)
                result += 1;
        }
        return result;
    }


//    @Benchmark
//    @OutputTimeUnit(TimeUnit.MILLISECONDS)
//    public int baseline() throws IOException {
//        int result = 0;
//        for (int ord = bitSet.nextSetBit(0); ord != DocIdSetIterator.NO_MORE_DOCS; ord = ord + 1 >= bitSet.length() ? DocIdSetIterator.NO_MORE_DOCS : bitSet.nextSetBit(ord + 1)) {
//            result++;
//        }
//        return result;
//    }
//
    @Benchmark
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public int numbersQuery() throws IOException {
        return evalRangeQueryResult(getResultSet(name));
    }

//    @Benchmark
//    @OutputTimeUnit(TimeUnit.MILLISECONDS)
//    public int numbersQuery2() throws IOException {
//        return evalRangeQueryResult(getResultSet2(name));
//    }

//    @Benchmark
//    @OutputTimeUnit(TimeUnit.MILLISECONDS)
//    public int docValues() throws IOException {
//        return evalDocValues(name);
//    }

    private DocIdSetIterator getResultSet(String storeType) throws IOException {
        Query query = new DocValuesNumbersQuery(getStore(storeType), 1L);
        query = query.rewrite(reader);
        Weight weight = query.createWeight(searcher, false, 0);
        Scorer scorer = weight.scorer(leafReaderContext);
        return scorer.iterator();
    }

    private DocIdSetIterator getResultSet2(String storeType) throws IOException {
        Query query = new DocValuesNumbersQuery2(getStore(storeType), 1L);
        query = query.rewrite(reader);
        Weight weight = query.createWeight(searcher, false, 0);
        Scorer scorer = weight.scorer(leafReaderContext);
        return scorer.iterator();
    }

    public static void main(String[] args) throws Exception {

        Options options = new OptionsBuilder()
                .include(SynteticDocValuesBench54.class.getName())
                .forks(0)
//                .addProfiler(LinuxPerfAsmProfiler.class)
//                .addProfiler(StackProfiler.class)
//                .addProfiler(LinuxPerfProfiler.class)
//                .addProfiler(GCProfiler.class)
                .verbosity(VerboseMode.NORMAL)
                .build();
        new Runner(options).run();
    }
}