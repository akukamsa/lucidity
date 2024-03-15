import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CartOfferApplicationTests {

    @Test
    public void applyMultipleFlatAmountOffersForSameSegment() throws Exception {
        String segment = "p1";
        List<OfferRequest> offerRequests = new ArrayList<>();
        
        // Adding multiple FLAT amount off offers for the same segment
        offerRequests.add(new OfferRequest(1, "FLATX", 10, segment));
        offerRequests.add(new OfferRequest(2, "FLATX", 20, segment));
        offerRequests.add(new OfferRequest(3, "FLATX", 30, segment));

        boolean result = addMultipleOffers(offerRequests);
        Assert.assertEquals(true, result); // able to add multiple offers
    }

    public boolean addMultipleOffers(List<OfferRequest> offerRequests) throws Exception {
        String urlString = "http://localhost:9001/api/v1/offer";

        for (OfferRequest offerRequest : offerRequests) {
            URL url = new URL(urlString);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setDoOutput(true);
            con.setRequestProperty("Content-Type", "application/json");

            ObjectMapper mapper = new ObjectMapper();
            String POST_PARAMS = mapper.writeValueAsString(offerRequest);

            OutputStream os = con.getOutputStream();
            os.write(POST_PARAMS.getBytes());
            os.flush();
            os.close();

            int responseCode = con.getResponseCode();
            System.out.println("POST Response Code :: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) { //success
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                // print result
                System.out.println(response.toString());
            } else {
                System.out.println("POST request did not work.");
                return false;
            }
        }
        return true;
    }
}
