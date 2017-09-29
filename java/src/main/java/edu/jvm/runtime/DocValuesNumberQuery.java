package edu.jvm.runtime;

import org.apache.lucene.index.DocValues;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.NumericDocValues;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.RandomAccessWeight;
import org.apache.lucene.search.Weight;
import org.apache.lucene.util.Bits;

import java.io.IOException;
import java.util.Objects;

public class DocValuesNumberQuery extends Query {

 private final String field;
  private final long number;

 public DocValuesNumberQuery(String field, long number) {
    this.field = Objects.requireNonNull(field);
    this.number = number;
  }

 @Override
  public boolean equals(Object obj) {
    if (!super.equals(obj)) {
      return false;
    }
    DocValuesNumberQuery that = (DocValuesNumberQuery) obj;
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
    return new RandomAccessWeight(this) {

     @Override
      protected Bits getMatchingDocs(final LeafReaderContext context) throws IOException {
        final NumericDocValues values = DocValues.getNumeric(context.reader(), field);
        final Bits docsWithField = context.reader().getDocsWithField(field);
        return new Bits() {

         @Override
          public boolean get(int doc) {
            return docsWithField != null && docsWithField.get(doc) && values.get(doc) == number;
          }

         @Override
          public int length() {
            return context.reader().maxDoc();
          }
        };
      }
    };
  }
}