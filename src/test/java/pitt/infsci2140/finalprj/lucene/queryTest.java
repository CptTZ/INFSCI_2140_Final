package pitt.infsci2140.finalprj.lucene;

import org.junit.Test;
import pitt.infsci2140.finalprj.controller.search.vo.SearchResultBean;
import pitt.infsci2140.finalprj.service.OriginalSearchService;

import java.util.List;

public class queryTest {

    private OriginalSearchService ss = new OriginalSearchService();

    @Test
    public void testSearch() {
        searchImpl(null);
        searchImpl("");
        searchImpl("Chicken");
        searchImpl("Chicken wings");
        searchImpl("\"Chicken wings\"");
        searchImpl("Chicken && wings");
        searchImpl("Chinese");
    }

    private void searchImpl(String data) {
        List<SearchResultBean> search = ss.queryByTerm(data, 10);
        outputLog(String.format("Term <%s> has <%s> hits", data, search.size()));
        for (SearchResultBean s : search) {
            outputLog(String.format("Comment ID: <%s>, name: '%s', address: <%s>, score: %f",
                    s.getCommentId(), s.getName(), s.getAddress(), s.getScore()));
            outputLog(null);
        }
    }

    private void outputLog(String data) {
        if (data == null) {
            System.err.println();
            return;
        }
        System.err.println(data);
    }

}
