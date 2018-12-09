package pitt.infsci2140.finalprj.lucene;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.junit.Assert;
import org.junit.Test;
import pitt.infsci2140.finalprj.controller.search.vo.SearchResultBean;
import pitt.infsci2140.finalprj.misc.Config;
import pitt.infsci2140.finalprj.service.OriginalSearchService;

import java.nio.file.Paths;
import java.util.List;

public class queryTest {

    private OriginalSearchService ss = new OriginalSearchService();

    @Test
    public void testSearch() {
        Assert.assertEquals(0, searchImpl(null));
        searchImpl("");
        searchImpl("Chicken");
        searchImpl("Chicken wings");
        searchImpl("\"Chicken wings\"");
        searchImpl("Chicken && wings");
        searchImpl("Chinese");
    }

    @Test
    public void testSearchBid() throws Exception {
        IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(FSDirectory.open(Paths.get(Config.LUCENE_ORIGINAL_INDEX_PATH))));
        Term t = new Term(Config.INDEXER_BUSS_ID, "UmZnFzo-NK2daxkl3_Rieg");
        TermQuery tq = new TermQuery(t);
        TopDocs td = searcher.search(tq, 100);
        Assert.assertEquals(5, td.totalHits);
    }

    private int searchImpl(String data) {
        List<SearchResultBean> search = ss.queryByTerm(data, 10);
        outputLog(String.format("Term <%s> has <%s> hits", data, search.size()));
        for (SearchResultBean s : search) {
            outputLog(String.format("Comment ID: <%s>, name: '%s', address: <%s>, score: %f",
                    s.getCommentId(), s.getName(), s.getAddress(), s.getScore()));
            outputLog(null);
        }
        return search.size();
    }

    private void outputLog(String data) {
        if (data == null) {
            System.err.println();
            return;
        }
        System.err.println(data);
    }

}
