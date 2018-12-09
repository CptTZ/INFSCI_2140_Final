package pitt.infsci2140.finalprj.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import okhttp3.CacheControl;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pitt.infsci2140.finalprj.misc.Config;
import pitt.infsci2140.finalprj.model.BusinessInfo;

import java.io.IOException;
import java.util.HashMap;

@Service
public class BusinessService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final HashMap<String, BusinessInfo> cacheInfo = new HashMap<>(200);

    @Value("${yelp}")
    private String yelpKey;

    public BusinessInfo getBusinessInfoById(String bid) {
        if (!this.cacheInfo.containsKey(bid)) {
            this.cacheInfo.put(bid, getRawBusinessInfoById(bid));
        }
        BusinessInfo b = this.cacheInfo.get(bid);
        if (b.getId() == null || b.getId().isEmpty()) {
            // Prob network issues, try get again
            this.cacheInfo.put(bid, getRawBusinessInfoById(bid));
            return this.cacheInfo.get(bid);
        } else {
            // Good result
            return b;
        }
    }

    private BusinessInfo getRawBusinessInfoById(String bid) {
        BusinessInfo b = new BusinessInfo();
        String jsonData = sendRequestToAPI(bid);
        if (jsonData.isEmpty()) return b;
        b.setId(bid);
        try {
            JsonNode root = Config.JSON_MAPPER.readTree(jsonData);
            b.setName(root.get("name").asText().trim());
            b.setUrl(root.get("url").asText().trim());
            b.setDisplay_phone(root.get("display_phone").asText().trim());
            StringBuilder addr = new StringBuilder();
            JsonNode addrNode = root.get("location").get("display_address");
            if (addrNode != null) {
                int s = addrNode.size();
                for (int i = 0; i < s; i++) {
                    addr.append(addrNode.get(i).asText());
                    addr.append(' ');
                }
            }
            b.setSimpleAddress(addr.toString());
            JsonNode coord = root.get("coordinates");
            b.setLat(coord.get("latitude").asDouble());
            b.setLng(coord.get("longitude").asDouble());
        } catch (Exception e) {
            logger.error("JSON parse error", e);
        }
        return b;
    }

    private String sendRequestToAPI(String bid) {
        Request r = genRequest(bid);
        try (Response response = Config.HTTP_CLIENT.newCall(r).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                throw new IOException("Unexpected code " + response);
            }
            return response.body().string();
        } catch (IOException e) {
            logger.error("Call to Yelp API error", e);
        }
        return "";
    }

    private Request genRequest(String bid) {
        return new Request.Builder()
                .url(Config.YELP_API_ENDPOINT + bid.trim())
                .addHeader("Authorization", yelpKey)
                .cacheControl(CacheControl.FORCE_NETWORK)
                .get()
                .build();
    }

}
