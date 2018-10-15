package pitt.infsci2140.finalprj.lucene;

import org.apache.lucene.document.Document;
import org.junit.Test;
import pitt.infsci2140.finalprj.service.SearchService;

import java.util.ArrayList;

public class queryTest {

    private SearchService ss = new SearchService();

    @Test
    public void testSearch() {
        searchImpl("Chicken");
        searchImpl("Chicken wings");
        searchImpl("\"Chicken wings\"");
        searchImpl("Chinese");
    }

    private void searchImpl(String data) {
        Object[] search = ss.queryByTerm(data, 10);
        outputLog(String.format("Term <%s> has <%s> hits", data, String.valueOf(search[0])));
        if (search[1] != null) {
            ArrayList<Document> docs = (ArrayList<Document>) search[1];
            ArrayList<Float> scores = (ArrayList<Float>) search[2];
            for (int i = 0; i < docs.size(); i++) {
                Document foundDoc = docs.get(i);
                outputLog(String.format("Comment ID: <%s>, name: '%s', address: <%s>, score: %f",
                        foundDoc.get("CID"), foundDoc.get("NAME"), foundDoc.get("ADDR"), scores.get(i)));
            }
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
