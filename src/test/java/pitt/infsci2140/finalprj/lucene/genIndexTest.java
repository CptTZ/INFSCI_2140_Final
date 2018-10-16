package pitt.infsci2140.finalprj.lucene;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Assert;
import org.junit.Test;
import pitt.infsci2140.finalprj.misc.Config;

import java.io.FileReader;
import java.io.Reader;
import java.nio.file.Paths;

/**
 * Generate new index
 */
public class genIndexTest {

    private FieldType metaFieldType;
    private Directory directory;
    private IndexWriter ixwriter;

    @Test
    public void genNewTest() throws Exception {
        this.metaFieldType = genFieldTypeMeta();
        this.ixwriter = genIxWriter(new BM25Similarity());
        Reader in = new FileReader("./pgh_review.csv");
        Iterable<CSVRecord> records = CSVFormat.EXCEL.withFirstRecordAsHeader().parse(in);
        for (CSVRecord record : records) {
            Document doc = genDocFromCSVRecord(record);
            ixwriter.addDocument(doc);
        }
        this.ixwriter.close();
        this.directory.close();
    }

    private IndexWriter genIxWriter(Similarity s) throws Exception {
        this.directory = FSDirectory.open(Paths.get(Config.LUCENE_INDEX_PATH));
        IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
        if (s != null) config.setSimilarity(s);
        return new IndexWriter(this.directory, config);
    }

    private FieldType genFieldTypeMeta() {
        FieldType fieldType = new FieldType();
        fieldType.setStored(true);
        fieldType.setTokenized(false);
        fieldType.setOmitNorms(true);
        return fieldType;
    }

    private Document genDocFromCSVRecord(CSVRecord record) {
        Document doc = new Document();

        String cid = record.get("comment_id");
        String name = record.get("name");
        String addr = record.get("address");
        String txt = record.get("comment_text");

        Assert.assertNotNull(cid);
        Assert.assertNotNull(name);
        Assert.assertNotNull(addr);
        Assert.assertNotNull(txt);

        doc.add(new Field("CID", cid, this.metaFieldType));
        doc.add(new Field("NAME", name, this.metaFieldType));
        doc.add(new Field("ADDR", addr, this.metaFieldType));
        doc.add(new TextField("TEXT", txt, Field.Store.NO));
        return doc;
    }

}
