package pitt.infsci2140.finalprj.lucene;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;
import pitt.infsci2140.finalprj.misc.Config;

import java.nio.file.Paths;

public class queryTest {

    private Directory directory;
    private DirectoryReader ireader;
    private IndexSearcher isearcher;

    @Test
    public void bm25QueryTest() throws Exception {
        this.directory = FSDirectory.open(Paths.get(Config.LUCENE_INDEX_PATH));
        this.ireader = DirectoryReader.open(directory);
        this.isearcher = genIxSearcher(this.ireader);

        searchTerm("Chicken");
        searchTerm("Chicken wings");
        searchTerm("\"Chicken wings\"");
        searchTerm("Chinese");

        this.directory.close();
    }

    private void searchTerm(String term) throws Exception {
        QueryParser qp = new QueryParser("TEXT", new StandardAnalyzer());
        Query q = qp.parse(term);
        TopDocs tops = isearcher.search(q, 10);
        outputLog(String.format("For term <%s>, total results: %d", term, tops.totalHits));

        ScoreDoc[] docs = tops.scoreDocs;
        for (ScoreDoc doc : docs) {
            int id = doc.doc;
            Document foundDoc = ireader.document(id);
            outputLog(String.format("Comment ID: <%s>, name: '%s', address: <%s>, score: %f",
                    foundDoc.get("CID"), foundDoc.get("NAME"), foundDoc.get("ADDR"), doc.score));
        }
        outputLog(null);
    }

    private IndexSearcher genIxSearcher(DirectoryReader reader) {
        IndexSearcher searcher = new IndexSearcher(reader);
        searcher.setSimilarity(new BM25Similarity());
        return searcher;
    }

    private void outputLog(String data) {
        if (data == null) {
            System.err.println();
            return;
        }
        System.err.println(data);
    }

}
