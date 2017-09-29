package edu.jvm.runtime;

import org.apache.lucene.index.DocValues;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.NumericDocValues;
import org.apache.lucene.search.*;

import java.io.IOException;
import java.util.Objects;

/**
 * Great thanks to Ivan M for such amazing implementation!!!
 */
public class DocValuesIvanQuery extends Query {

    private final String field;
    private final long number;

    public DocValuesIvanQuery(String field, long number) {
        this.field = Objects.requireNonNull(field);
        this.number = number;
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        DocValuesIvanQuery that = (DocValuesIvanQuery) obj;
        return field.equals(that.field) && number == that.number;
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Objects.hash(field, number);
    }

    @Override
    public String toString(String defaultField) {
        return field + ":" + number;
    }

    @Override
    public Weight createWeight(IndexSearcher searcher, boolean needsScores) throws IOException {
        return new ConstantScoreWeight(this) {

            @Override
            public Scorer scorer(LeafReaderContext context) throws IOException {
                final NumericDocValues values = DocValues.getNumeric(context.reader(), field);
                final int maxDoc = context.reader().numDocs();
                return new ConstantScoreScorer(this, score(), new DocIdSetIterator() {

                    int doc = 0;

                    @Override
                    public int docID() {
                        return doc;
                    }

                    @Override
                    public int nextDoc() throws IOException {
                        for (int i = doc + 1; i < maxDoc; i++) {
                            if (values.get(i) == 1) {
                                doc = i;
                                return i;
                            }
                        }
                        return DocIdSetIterator.NO_MORE_DOCS;
                    }

                    @Override
                    public int advance(int target) throws IOException {
                        for (int i = target; i < maxDoc; i++) {
                            if (values.get(i) == 1) {
                                doc = i;
                                return i;
                            }
                        }
                        return DocIdSetIterator.NO_MORE_DOCS;
                    }

                    @Override
                    public long cost() {
                        return Integer.MAX_VALUE;
                    }
                });
            }
        };
    }
}