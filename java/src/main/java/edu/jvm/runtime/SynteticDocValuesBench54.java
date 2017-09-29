package edu.jvm.runtime;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.NumericDocValuesField;
import org.apache.lucene.document.StringField;
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
import java.util.concurrent.TimeUnit;


@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 1, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 2, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(value = 1, jvmArgs = {
//        "-XX:+UnlockDiagnosticVMOptions",
//        "-XX:+PrintInlining",
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

    @Param({"65", "99", "44", "45"})
    private int store;

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
    }

    /**
     * Creates a small copy of original index with column variations.
     */
    private void copyColumn() throws IOException {
        FSDirectory originalIndexDirectory = FSDirectory.open(new File(INDEX_PATH).toPath());
        DirectoryReader originalReader = DirectoryReader.open(originalIndexDirectory);
        IndexSearcher originalSearcher = new IndexSearcher(originalReader);
        List<LeafReaderContext> leaves = originalReader.leaves();
        LeafReaderContext leafReaderContext = leaves.get(0);


        FSDirectory directory = FSDirectory.open(new File(TEMPORARY_PATH).toPath());
        IndexWriterConfig conf = new IndexWriterConfig(null);
        conf.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        conf.setUseCompoundFile(false);
        conf.setMaxBufferedDocs(1000000);
        conf.setRAMBufferSizeMB(2048);
        IndexWriter writer = new IndexWriter(directory, conf);

        Query query = DocValuesRangeQuery.newLongRange(getStore(STORE_AS_IS), 1L, 1L, true, true);
        query = query.rewrite(originalReader);
        Weight weight = query.createWeight(originalSearcher, false);
        Scorer scorer = weight.scorer(leafReaderContext);
        DocIdSetIterator docs = scorer.iterator();
        int document;

        int maxDoc = originalReader.numDocs();
        bitSet = new FixedBitSet(maxDoc);
        while ((document = docs.nextDoc()) != DocIdSetIterator.NO_MORE_DOCS) {
            bitSet.set(document);
        }

        for (int i = 0; i < maxDoc; i++) {

            Document doc = new Document();
            doc.add(new StringField("id", "" + i, Field.Store.NO));

            int value = bitSet.get(i) ? 1 : 0;
            if (value == 1) {
                doc.add(new NumericDocValuesField(STORE_ONE_VAL + store, value));
                doc.add(new NumericDocValuesField(STORE_SPARSE + store, value));
            } else if (i == 0) {
                doc.add(new NumericDocValuesField(STORE_SPARSE + store, 0)); //we have two unique values
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

    private int evalRangeQueryResult(String columnType) throws IOException {
        int result;
        DocIdSetIterator docs = getResultSet(columnType);
        result = 0;
        while (docs.nextDoc() != DocIdSetIterator.NO_MORE_DOCS) {
            result++;
        }
        return result;
    }

    private int evalDocValues(String columnType) throws IOException {
        int result = 0;
        NumericDocValues numericDocValues = DocValues.getNumeric(leafReaderContext.reader(), getStore(columnType));
        for (int j = 0; j < maxDoc; j++) {
            long value = numericDocValues.get(j);
            result += value;
        }
        return result;
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public int iterateAllAsIs() throws IOException {
        return evalRangeQueryResult(STORE_AS_IS);
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public int iterateAllOneVal() throws IOException {
        return evalRangeQueryResult(STORE_ONE_VAL);
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public int iterateAllSparse() throws IOException {
        return evalRangeQueryResult(STORE_SPARSE);
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public int iterateAllAsIsDV() throws IOException {
        return evalDocValues(STORE_AS_IS);
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public int iterateAllOneValDV() throws IOException {
        return evalDocValues(STORE_ONE_VAL);
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public int iterateAllSparseDV() throws IOException {
        return evalDocValues(STORE_SPARSE);
    }

    private DocIdSetIterator getResultSet(String storeType) throws IOException {
        Query query = DocValuesRangeQuery.newLongRange(getStore(storeType), 1L, 1L, true, true);
        query = query.rewrite(reader);
        Weight weight = query.createWeight(searcher, false);
        Scorer scorer = weight.scorer(leafReaderContext);
        return scorer.iterator();
    }

    public static void main(String[] args) throws Exception {

        Options options = new OptionsBuilder()
                .include(SynteticDocValuesBench54.class.getName())
//                .addProfiler(LinuxPerfAsmProfiler.class)
//                .addProfiler(StackProfiler.class)
//                .addProfiler(LinuxPerfProfiler.class)
//                .addProfiler(GCProfiler.class)
                .verbosity(VerboseMode.NORMAL)
                .build();
        new Runner(options).run();
    }
}