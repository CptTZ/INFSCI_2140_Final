package pitt.infsci2140.finalprj.service;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pitt.infsci2140.finalprj.misc.Config;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

@Service
public class SearchService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private IndexSearcher indexSearcher;
    private DirectoryReader reader;
    private final QueryParser queryParser = new QueryParser(Config.INDEXER_COMMENT_TXT, new StandardAnalyzer());

    public SearchService() {
    }

    /**
     * Submit a query
     *
     * @return 0: total match; 1: Documents (In a list); 2: Score (In a list)
     */
    public Object[] queryByTerm(String term, int resultLimit) {
        Object[] res;
        try {
            res = generateResultDocs(term, resultLimit);
        } catch (Exception e) {
            logger.error("Search term failed", e);
            res = new Object[]{0, null, null};
        } finally {
            stopSearch();
        }
        return res;
    }

    private Object[] generateResultDocs(String term, int resultLimit) throws ParseException, IOException {
        TopDocs topDocs = searchTerm(term, resultLimit);
        long totalHits = topDocs.totalHits;
        int listSize = (int) (resultLimit > totalHits ? totalHits : resultLimit);
        ArrayList<Document> documents = new ArrayList<>(listSize);
        ArrayList<Float> scores = new ArrayList<>(listSize);
        ScoreDoc[] docs = topDocs.scoreDocs;
        for (ScoreDoc doc : docs) {
            documents.add(reader.document(doc.doc));
            scores.add(doc.score);
        }
        return new Object[]{totalHits, documents, scores};
    }

    private void prepSearch() throws IOException {
        this.reader = DirectoryReader.open(FSDirectory.open(Paths.get(Config.LUCENE_INDEX_PATH)));
        this.indexSearcher = genIdxSearcher(Config.PROJECT_DEFAULT_SIM);
    }

    private void stopSearch() {
        if (isSearcherNotReady()) return;
        try {
            this.reader.close();
        } catch (IOException e) {
            logger.error("Reader fail to close", e);
        } finally {
            this.reader = null;
            this.indexSearcher = null;
        }
    }

    private TopDocs searchTerm(String term, int limit) throws ParseException, IOException {
        if (isSearcherNotReady()) prepSearch();
        TopDocs tops = indexSearcher.search(queryParser.parse(term), limit);

        logger.debug(String.format("Term <%s>, result#: %d", term, tops.totalHits));
        return tops;
    }

    private boolean isSearcherNotReady() {
        return (this.reader == null || this.indexSearcher == null);
    }

    private IndexSearcher genIdxSearcher(Similarity s) {
        IndexSearcher searcher = new IndexSearcher(this.reader);
        if (s != null) searcher.setSimilarity(s);
        return searcher;
    }

}
