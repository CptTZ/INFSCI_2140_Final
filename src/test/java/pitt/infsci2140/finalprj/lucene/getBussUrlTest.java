package pitt.infsci2140.finalprj.lucene;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import pitt.infsci2140.finalprj.misc.Config;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class getBussUrlTest {

    @Value("${yelp}")
    private String yelpKey;

    @Test
    public void basicTest() throws IOException {
        Request request = new Request.Builder()
                .url(Config.yelpApiPath + "UmZnFzo-NK2daxkl3_Rieg")
                .addHeader("Authorization", yelpKey).get().build();
        try (Response response = Config.httpClient.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                throw new IOException("Unexpected code " + response);
            }
            String d = response.body().string();
            ObjectMapper objectMapper = new ObjectMapper();
            String url = objectMapper.readTree(d).get("url").asText().trim();
            Assert.assertEquals("https://www.yelp.com/biz/philip-pelusi-pittsburgh-15?adjust_creative=1PRRy1S_44ISezZLR22uaA&utm_campaign=yelp_api_v3&utm_medium=api_v3_business_lookup&utm_source=1PRRy1S_44ISezZLR22uaA", url);
        }
    }

}
