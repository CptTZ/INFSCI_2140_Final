package pitt.infsci2140.finalprj.controller.search;

import org.apache.lucene.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import pitt.infsci2140.finalprj.controller.search.vo.SearchBean;
import pitt.infsci2140.finalprj.controller.search.vo.SearchResultBean;
import pitt.infsci2140.finalprj.misc.Config;
import pitt.infsci2140.finalprj.service.OriginalSearchService;

import java.util.ArrayList;
import java.util.List;

@Controller
public class SearchController {

    private final OriginalSearchService originalSearchService;

    @Autowired
    public SearchController(OriginalSearchService originalSearchService) {
        this.originalSearchService = originalSearchService;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("s", new SearchBean());
        return "search";
    }

    @PostMapping("/")
    public ModelAndView greetingSubmit(@ModelAttribute SearchBean search, ModelAndView model) {
        List<SearchResultBean> res = originalSearchService.queryByTerm(search.getQuery(), 20);
        if (res.size() == 0) {
            // No result, redo search
            model.setViewName("searchResult");
            model.addObject("hasRes", false);
            model.addObject("retS", search);
            return model;
        }
        model.setViewName("searchResult");
        model.addObject("hasRes", true);
        model.addObject("results", res);
        model.addObject("retS", search);
        return model;
    }

}
