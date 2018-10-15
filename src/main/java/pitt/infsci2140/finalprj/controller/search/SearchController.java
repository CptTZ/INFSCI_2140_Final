package pitt.infsci2140.finalprj.controller.search;

import org.apache.lucene.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import pitt.infsci2140.finalprj.controller.search.vo.SearchResultBean;
import pitt.infsci2140.finalprj.controller.search.vo.SearchSubmissionBean;
import pitt.infsci2140.finalprj.service.SearchService;

import java.util.ArrayList;

@Controller
public class SearchController {

    @Autowired
    private SearchService searchService;

    @PostMapping("/api/search")
    @ResponseBody
    public ArrayList<SearchResultBean> greetingSubmit(@RequestBody SearchSubmissionBean search) {
        Object[] res = searchService.queryByTerm(search.getQuery(), 10);
        ArrayList<SearchResultBean> srb = new ArrayList<>(0);
        if (res[1] != null) {
            ArrayList<Document> docs = (ArrayList<Document>) res[1];
            ArrayList<Float> scores = (ArrayList<Float>) res[2];
            srb = new ArrayList<>(docs.size());
            for (int i = 0; i < docs.size(); i++) {
                Document foundDoc = docs.get(i);
                SearchResultBean s = new SearchResultBean();
                s.setAddress(foundDoc.get("ADDR"));
                s.setCommentId(foundDoc.get("CID"));
                s.setName(foundDoc.get("NAME"));
                s.setScore(scores.get(i));
                srb.add(s);
            }
        }
        return srb;
    }

}
