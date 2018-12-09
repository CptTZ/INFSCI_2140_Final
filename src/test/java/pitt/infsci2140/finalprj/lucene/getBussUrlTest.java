package pitt.infsci2140.finalprj.lucene;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import pitt.infsci2140.finalprj.service.BusinessService;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class getBussUrlTest {

    @Value("${yelp}")
    private String yelpKey;

    @Autowired
    private BusinessService bs;

    @Test
    public void basicTest() throws IOException {
        String url = bs.getBusinessInfoById("UmZnFzo-NK2daxkl3_Rieg").getUrl();
        Assert.assertEquals("https://www.yelp.com/biz/philip-pelusi-pittsburgh-15?adjust_creative=1PRRy1S_44ISezZLR22uaA&utm_campaign=yelp_api_v3&utm_medium=api_v3_business_lookup&utm_source=1PRRy1S_44ISezZLR22uaA", url);
    }

}
