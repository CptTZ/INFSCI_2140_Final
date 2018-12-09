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
import pitt.infsci2140.finalprj.controller.search.vo.SearchResultBean;
import pitt.infsci2140.finalprj.misc.Config;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class OriginalSearchService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private IndexSearcher indexSearcher;
    private DirectoryReader reader;
    private final QueryParser queryParser = new QueryParser(Config.INDEXER_COMMENT_TXT, new StandardAnalyzer());

    public OriginalSearchService() {
    }

    public List<SearchResultBean> queryByTerm(String term, int resultLimit) {
        if (term == null || term.isEmpty()) return new ArrayList<>(0);
        try {
            TopDocs topDocs = searchTerm(term, resultLimit);
            ScoreDoc[] docs = topDocs.scoreDocs;
            int totalHits = Math.toIntExact(topDocs.totalHits);

            int listSize = resultLimit > totalHits ? totalHits : resultLimit;
            ArrayList<SearchResultBean> res = new ArrayList<>(listSize);

            for (ScoreDoc doc : docs) {
                Document oneDoc = reader.document(doc.doc);
                SearchResultBean s = new SearchResultBean();
                s.setAddress(oneDoc.get(Config.INDEXER_SHOP_ADDRESS));
                s.setCommentId(oneDoc.get(Config.INDEXER_COMMENT_ID));
                s.setName(oneDoc.get(Config.INDEXER_SHOP_NAME));
                s.setScore(doc.score);
                res.add(s);
            }

            return res;
        } catch (Exception e) {
            logger.error("Search term failed", e);
            return new ArrayList<>(0);
        }
    }

    private void prepSearch() throws IOException {
        if (!isSearcherNotReady()) return;
        this.reader = DirectoryReader.open(FSDirectory.open(Paths.get(Config.LUCENE_ORIGINAL_INDEX_PATH)));
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

        logger.debug("Term {}, result#: {}", term, tops.totalHits);
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
