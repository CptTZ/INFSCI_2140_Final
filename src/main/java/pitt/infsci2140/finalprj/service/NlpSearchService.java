package pitt.infsci2140.finalprj.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NlpSearchService extends OriginalSearchService {

    @Autowired
    public NlpSearchService(BusinessService bs) {
        super(bs);
    }


}
