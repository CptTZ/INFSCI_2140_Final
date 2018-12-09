package pitt.infsci2140.finalprj.service;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
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
import java.util.HashSet;
import java.util.List;

@Service
public class OriginalSearchService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final BusinessService businessService;

    private IndexSearcher normIndexSearcher;
    private DirectoryReader normReader;
    final QueryParser queryParser = new QueryParser(Config.INDEXER_COMMENT_TXT, new StandardAnalyzer());

    @Autowired
    public OriginalSearchService(BusinessService bs) {
        this.businessService = bs;
    }

    public List<SearchResultBean> queryNormalByTerm(String term, int businessLimit) {
        if (term == null || term.isEmpty()) return new ArrayList<>(0);
        try {
            TopDocs topDocs = searchTerm(term, businessLimit * 10);
            ScoreDoc[] docs = topDocs.scoreDocs;
            int totalHits = Math.toIntExact(topDocs.totalHits);

            int listSize = businessLimit > totalHits ? totalHits : businessLimit;
            ArrayList<SearchResultBean> res = new ArrayList<>(listSize);
            HashSet<String> businessIdSet = new HashSet<>(listSize);

            for (ScoreDoc doc : docs) {
                String bid = getBusinessIdByDocId(doc.doc);
                if (businessIdSet.size() == listSize) break;
                if (businessIdSet.contains(bid)) continue;
                res.add(bidToSearchResultBean(bid, doc.score));
                businessIdSet.add(bid);
            }

            return res;
        } catch (Exception e) {
            logger.error("Search normal failed", e);
            return new ArrayList<>(0);
        }
    }

    private void prepSearch() throws IOException {
        if (!isSearcherNotReady()) return;
        this.normReader = DirectoryReader.open(FSDirectory.open(Paths.get(Config.LUCENE_ORIGINAL_INDEX_PATH)));
        this.normIndexSearcher = genIdxSearcher(this.normReader, Config.PROJECT_DEFAULT_SIM);
    }

    private boolean isSearcherNotReady() {
        return (this.normReader == null || this.normIndexSearcher == null);
    }

    IndexSearcher genIdxSearcher(IndexReader reader, Similarity s) {
        IndexSearcher searcher = new IndexSearcher(reader);
        if (s != null) searcher.setSimilarity(s);
        return searcher;
    }

    private TopDocs searchTerm(String term, int limit) throws ParseException, IOException {
        if (isSearcherNotReady()) prepSearch();
        TopDocs tops = this.normIndexSearcher.search(queryParser.parse(term), limit);

        logger.debug("Term {}, result#: {}", term, tops.totalHits);
        return tops;
    }

    private String getBusinessIdByDocId(int docid) throws IOException {
        return this.normIndexSearcher.doc(docid).get(Config.INDEXER_BUSS_ID);
    }

    SearchResultBean bidToSearchResultBean(String bid, float score) {
        SearchResultBean s = new SearchResultBean();
        BusinessInfo bInfo = this.businessService.getBusinessInfoById(bid);
        String addr = bInfo.getSimpleAddress();
        s.setAddress(addr.isEmpty() ? "Unknown" : addr);
        s.setName(bInfo.getName());
        s.setUrl(bInfo.getUrl());
        s.setPhone(bInfo.getDisplay_phone());
        s.setScore(score);
        return s;
    }

}
