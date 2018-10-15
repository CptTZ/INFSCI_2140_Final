package pitt.infsci2140.finalprj.lucene;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

import java.io.FileReader;
import java.io.Reader;
import java.nio.file.Paths;

/**
 * Generate new index
 */
public class genIndexTest {

    private FieldType type;
    private Directory directory;
    private IndexWriter ixwriter;

    @Test
    public void genNewTest() throws Exception {
        this.type = genFieldType();
        this.ixwriter = genIxWriter();
        Reader in = new FileReader("./pgh_review.csv");
        Iterable<CSVRecord> records = CSVFormat.EXCEL.withFirstRecordAsHeader().parse(in);
        for (CSVRecord record : records) {
            Document doc = genDocFromCSVRecord(record);
            ixwriter.addDocument(doc);
        }
        this.ixwriter.close();
        this.directory.close();
    }

    private IndexWriter genIxWriter() throws Exception {
        this.directory = FSDirectory.open(Paths.get("./lucene/index"));
        IndexWriterConfig indexConfig = new IndexWriterConfig(new StandardAnalyzer());
        indexConfig.setMaxBufferedDocs(10000);
        return new IndexWriter(this.directory, indexConfig);
    }

    private FieldType genFieldType() {
        FieldType fieldType = new FieldType();
        fieldType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
        fieldType.setStored(false);
        fieldType.setStoreTermVectors(true);
        return fieldType;
    }

    private Document genDocFromCSVRecord(CSVRecord record) {
        Document doc = new Document();
        String comment_id = record.get("comment_id");
        String comment = record.get("comment_text");
        doc.add(new StoredField("DOCID", comment_id));
        doc.add(new Field("TEXT", comment, this.type));
        return doc;
    }

}
