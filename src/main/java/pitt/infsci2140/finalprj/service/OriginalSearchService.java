package pitt.infsci2140.finalprj.service;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pitt.infsci2140.finalprj.controller.search.vo.SearchResultBean;
import pitt.infsci2140.finalprj.misc.Config;
import pitt.infsci2140.finalprj.model.BusinessInfo;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class OriginalSearchService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final BusinessService businessService;

    private IndexSearcher indexSearcher;
    private DirectoryReader reader;
    private final QueryParser queryParser = new QueryParser(Config.INDEXER_COMMENT_TXT, new StandardAnalyzer());

    @Autowired
    public OriginalSearchService(BusinessService bs) {
        this.businessService = bs;
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
                res.add(docidToSearchResultBean(doc.doc, doc.score));
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

    private boolean isSearcherNotReady() {
        return (this.reader == null || this.indexSearcher == null);
    }

    private IndexSearcher genIdxSearcher(Similarity s) {
        IndexSearcher searcher = new IndexSearcher(this.reader);
        if (s != null) searcher.setSimilarity(s);
        return searcher;
    }

    private TopDocs searchTerm(String term, int limit) throws ParseException, IOException {
        if (isSearcherNotReady()) prepSearch();
        TopDocs tops = indexSearcher.search(queryParser.parse(term), limit);

        logger.debug("Term {}, result#: {}", term, tops.totalHits);
        return tops;
    }

    private SearchResultBean searchByBussinessId(String bid) throws IOException {
        if (bid == null || bid.isEmpty()) return null;
        TermQuery tq = new TermQuery(new Term(Config.INDEXER_BUSS_ID, bid));
        TopDocs t = this.indexSearcher.search(tq, 1);
        if (t.totalHits == 0L) return null;
        ScoreDoc d = t.scoreDocs[0];
        return docidToSearchResultBean(d.doc, d.score);
    }

    private SearchResultBean docidToSearchResultBean(int docid, float score) throws IOException {
        SearchResultBean s = new SearchResultBean();
        Document d = this.indexSearcher.doc(docid);
        BusinessInfo bInfo = this.businessService.getBusinessInfoById(d.get(Config.INDEXER_BUSS_ID));
        String addr = bInfo.getSimpleAddress();
        String name = bInfo.getName();
        s.setAddress(addr.isEmpty() ? d.get(Config.INDEXER_SHOP_ADDRESS) : addr);
        s.setName(name.isEmpty() ? d.get(Config.INDEXER_SHOP_NAME) : name);
        s.setUrl(bInfo.getUrl());
        s.setPhone(bInfo.getDisplay_phone());
        s.setScore(score);
        return s;
    }

}
