package pitt.infsci2140.finalprj.controller.search;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import pitt.infsci2140.finalprj.controller.search.vo.SearchBean;
import pitt.infsci2140.finalprj.controller.search.vo.SearchResultBean;
import pitt.infsci2140.finalprj.service.NlpSearchService;

import java.util.List;

@Controller
public class SearchController {

    private final NlpSearchService nlpSearchService;

    @Autowired
    public SearchController(NlpSearchService nlp) {
        this.nlpSearchService = nlp;
    }

    @GetMapping("/")
    public String homeSearch(Model model) {
        model.addAttribute("s", new SearchBean());
        return "search";
    }

    @PostMapping("/")
    public ModelAndView originalSearchResult(@ModelAttribute SearchBean search, ModelAndView model) {
        List<SearchResultBean> res = nlpSearchService.queryNormalByTerm(search.getQuery(), 15);
        return getModelAndView(search, model, res);
    }

    @PostMapping("/advSearch")
    public ModelAndView nlpSearchResult(@ModelAttribute SearchBean search, ModelAndView model) {
        List<SearchResultBean> res = nlpSearchService.queryNlpByTerm(search.getQuery(), 15);
        return getModelAndView(search, model, res);
    }

    private ModelAndView getModelAndView(@ModelAttribute SearchBean search, ModelAndView model, List<SearchResultBean> res) {
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
