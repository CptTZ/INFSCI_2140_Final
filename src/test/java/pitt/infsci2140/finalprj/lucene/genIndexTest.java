package pitt.infsci2140.finalprj.lucene;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import pitt.infsci2140.finalprj.misc.Config;

import java.io.FileReader;
import java.io.Reader;
import java.nio.file.Paths;

/**
 * Generate new index
 */
public class genIndexTest {

    private static final FieldType metaFieldType = new FieldType();
    private Directory directory;

    static {
        metaFieldType.setStored(true);
        metaFieldType.setTokenized(false);
        metaFieldType.setOmitNorms(true);
        metaFieldType.setIndexOptions(IndexOptions.NONE);
        metaFieldType.freeze();
    }

    @Test
    @Ignore
    public void genNewTest() throws Exception {
        IndexWriter ixwriter = genIxWriter(Config.PROJECT_DEFAULT_SIM);
        Reader in = new FileReader("./pgh_review.csv");
        Iterable<CSVRecord> records = CSVFormat.EXCEL.withFirstRecordAsHeader().parse(in);
        for (CSVRecord record : records) {
            Document doc = genDocFromCSVRecord(record);
            ixwriter.addDocument(doc);
        }
        ixwriter.close();
        this.directory.close();
        in.close();
    }

    private IndexWriter genIxWriter(Similarity s) throws Exception {
        this.directory = FSDirectory.open(Paths.get(Config.LUCENE_ORIGINAL_INDEX_PATH));
        IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
        if (s != null) config.setSimilarity(s);
        return new IndexWriter(this.directory, config);
    }

    private Document genDocFromCSVRecord(CSVRecord record) {
        Document doc = new Document();

        String bid = record.get("business_id");
        String cid = record.get("comment_id");
        String name = record.get("name");
        String addr = record.get("address");
        String txt = record.get("comment_text");
        Long cool = Long.valueOf(record.get("comment_cool"));
        Long useful = Long.valueOf(record.get("comment_useful"));
        Long fun = Long.valueOf(record.get("comment_funny"));
        Long star = Long.valueOf(record.get("comment_star"));

        Assert.assertNotNull(cid);
        Assert.assertNotNull(name);
        Assert.assertNotNull(addr);
        Assert.assertNotNull(txt);
        Assert.assertTrue(cool >= 0);
        Assert.assertTrue(useful >= 0);
        Assert.assertTrue(fun >= 0);
        Assert.assertTrue(star >= 0);

        doc.add(new StringField(Config.INDEXER_BUSS_ID, bid, Field.Store.YES));
        doc.add(new StringField(Config.INDEXER_COMMENT_ID, cid, Field.Store.YES));
        doc.add(new Field(Config.INDEXER_SHOP_NAME, name, metaFieldType));
        doc.add(new Field(Config.INDEXER_SHOP_ADDRESS, addr, metaFieldType));
        doc.add(new TextField(Config.INDEXER_COMMENT_TXT, txt, Field.Store.YES));
        doc.add(new SortedNumericDocValuesField(Config.INDEXER_NUM_COOL, cool));
        doc.add(new SortedNumericDocValuesField(Config.INDEXER_NUM_USEFUL, useful));
        doc.add(new SortedNumericDocValuesField(Config.INDEXER_NUM_FUN, fun));
        doc.add(new SortedNumericDocValuesField(Config.INDEXER_NUM_STAR, star));

        return doc;
    }

}
