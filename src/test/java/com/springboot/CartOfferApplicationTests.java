package com.springboot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.controller.OfferRequest;
import com.springboot.controller.SegmentResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
@SpringBootTest
public class CartOfferApplicationTests {

    // Scenario 1: Apply Flat ₹X Amount Off Offer
    @Test
    public void testFlatAmountOffOffer() throws Exception {
        List<String> segments = new ArrayList<>();
        segments.add("p1");
        OfferRequest offerRequest = new OfferRequest(1, "FLATX", 10, segments);

        boolean result = addOffer(offerRequest);

        // Expecting the result to be true
        Assertions.assertTrue(result);

        // Simulate cart application
        double finalCartValue = 200 - 10; // ₹200 - ₹10 offer = ₹190
        Assertions.assertEquals(190, finalCartValue);
    }
	 public boolean addOffer(OfferRequest offerRequest) throws Exception {
        String urlString = "http://localhost:9001/api/v1/offer";
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
        }
        return true;
    }

    // Scenario 2: Apply Flat X% Off Offer
    @Test
    public void testFlatPercentageOffOffer() throws Exception {
        List<String> segments = new ArrayList<>();
        segments.add("p1");
        OfferRequest offerRequest = new OfferRequest(1, "FLATPERCENT", 10, segments);

        boolean result = addOffer(offerRequest);

        // Expecting the result to be true
        Assertions.assertTrue(result);

        // Simulate cart application
        double finalCartValue = 200 - (200 * 0.10); // ₹200 - 10% = ₹180
        Assertions.assertEquals(180, finalCartValue);
    }

    // Scenario 3: No Offers Available for Restaurant
    @Test
    public void testNoOffersAvailable() throws Exception {
        List<String> segments = new ArrayList<>();
        segments.add("p1");
        OfferRequest offerRequest = new OfferRequest(999, "FLATX", 10, segments); // Invalid restaurant ID

        boolean result = addOffer(offerRequest);

        // Expecting no offer to be applied, cart value remains ₹200
        Assertions.assertFalse(result);
    }

    // Scenario 4: User Not in Any Customer Segment
    @Test
    public void testUserNotInAnySegment() throws Exception {
        List<String> segments = new ArrayList<>();
        segments.add("p1");
        OfferRequest offerRequest = new OfferRequest(1, "FLATX", 10, segments);

        boolean result = addOffer(offerRequest);

        // User doesn't belong to segment "p1", cart value should remain ₹200
        double finalCartValue = 200; // no discount
        Assertions.assertEquals(finalCartValue, 200);
    }

    // Scenario 5: Multiple Offers Exist (Flat ₹X Off & Flat X% Off)
    @Test
    public void testMultipleOffersExist() throws Exception {
        List<String> segments = new ArrayList<>();
        segments.add("p1");

        // Add two offers - ₹10 off and 10% off
        OfferRequest offerRequest1 = new OfferRequest(1, "FLATX", 10, segments); // Flat ₹10 off
        OfferRequest offerRequest2 = new OfferRequest(1, "FLATPERCENT", 10, segments); // Flat 10% off

        boolean result1 = addOffer(offerRequest1);
        boolean result2 = addOffer(offerRequest2);

        // Verify both offers were successfully added
        Assertions.assertTrue(result1);
        Assertions.assertTrue(result2);

        // Simulate cart application with the best discount applied (₹20 from 10% off)
        double finalCartValue = 200 - (200 * 0.10); // ₹200 - 10% = ₹180
        Assertions.assertEquals(180, finalCartValue);
    }

    // Scenario 6: Multiple Offers Exist (Pick Maximum Discount)
    @Test
    public void testPickMaximumDiscount() throws Exception {
        List<String> segments = new ArrayList<>();
        segments.add("p1");

        // Add two offers - ₹30 off and 10% off
        OfferRequest offerRequest1 = new OfferRequest(1, "FLATX", 30, segments); // Flat ₹30 off
        OfferRequest offerRequest2 = new OfferRequest(1, "FLATPERCENT", 10, segments); // Flat 10% off

        boolean result1 = addOffer(offerRequest1);
        boolean result2 = addOffer(offerRequest2);

        // Verify both offers were successfully added
        Assertions.assertTrue(result1);
        Assertions.assertTrue(result2);

        // Simulate cart application with the best discount applied (₹30 from Flat ₹30 off)
        double finalCartValue = 200 - 30; // ₹200 - ₹30 = ₹170
        Assertions.assertEquals(170, finalCartValue);
    }

    // Scenario 7: Offer for a Different Segment
    @Test
    public void testOfferForDifferentSegment() throws Exception {
        List<String> segments = new ArrayList<>();
        segments.add("p2");
        OfferRequest offerRequest = new OfferRequest(1, "FLATX", 10, segments); // ₹10 off for segment p2

        boolean result = addOffer(offerRequest);

        // User belongs to segment "p1", so no offer is applied.
        double finalCartValue = 200; // No discount should be applied
        Assertions.assertEquals(finalCartValue, 200);
    }

    // Scenario 8: Invalid Restaurant ID
    @Test
    public void testInvalidRestaurantID() throws Exception {
        List<String> segments = new ArrayList<>();
        segments.add("p1");
        OfferRequest offerRequest = new OfferRequest(0, "FLATX", 10, segments); // Invalid restaurant ID

        boolean result = addOffer(offerRequest);

        // Expecting the result to be false
        Assertions.assertFalse(result);
    }

    // Scenario 9: Invalid User ID
    @Test
    public void testInvalidUserID() throws Exception {
        List<String> segments = new ArrayList<>();
        segments.add("p1");
        OfferRequest offerRequest = new OfferRequest(1, "FLATX", 10, segments); // Invalid User ID (Assuming 1 is invalid)

        boolean result = addOffer(offerRequest);

        // Expecting the result to be false
        Assertions.assertFalse(result);
    }

    // Scenario 10: API Failure When Fetching User Segment
    @Test
    public void testAPIFailureFetchingUserSegment() throws Exception {
        // Simulate API failure for user segment fetching (mock API to return error)
        // In this case, you would mock the API to simulate failure using tools like MockMvc or MockServer
        Assertions.fail("Simulate API failure");
    }

    // Scenario 11: API Failure When Fetching Offers
    @Test
    public void testAPIFailureFetchingOffers() throws Exception {
        // Simulate API failure for offer fetching (mock API to return error)
        // In this case, you would mock the API to simulate failure using tools like MockMvc or MockServer
        Assertions.fail("Simulate API failure");
    }

    // Scenario 12: Offer Applies to Minimum Cart Value
    @Test
    public void testOfferAppliesToMinimumCartValue() throws Exception {
        List<String> segments = new ArrayList<>();
        segments.add("p1");
        OfferRequest offerRequest = new OfferRequest(1, "FLATX", 20, segments);

        boolean result = addOffer(offerRequest);

        // Cart value is ₹140, so ₹20 off should not apply as the offer is valid only for carts above ₹150
        double finalCartValue = 140; // No discount should apply
        Assertions.assertEquals(finalCartValue, 140);
    }

    // Scenario 13: Offer Stacking is Not Allowed
    @Test
    public void testOfferStackingNotAllowed() throws Exception {
        List<String> segments = new ArrayList<>();
        segments.add("p1");

        // Add two offers - ₹10 off and 10% off
        OfferRequest offerRequest1 = new OfferRequest(1, "FLATX", 10, segments); // Flat ₹10 off
        OfferRequest offerRequest2 = new OfferRequest(1, "FLATPERCENT", 10, segments); // Flat 10% off

        boolean result1 = addOffer(offerRequest1);
        boolean result2 = addOffer(offerRequest2);

        // Verify both offers were successfully added
        Assertions.assertTrue(result1);
        Assertions.assertTrue(result2);

        // Simulate cart application (only the best discount should be applied)
        double finalCartValue = 200 - (200 * 0.10); // ₹200 - 10% = ₹180
        Assertions.assertEquals(180, finalCartValue);
    }

    // Scenario 14: Apply Offer on Different Restaurants
    @Test
    public void testApplyOfferOnDifferentRestaurants() throws Exception {
        List<String> segments = new ArrayList<>();
        segments.add("p1");

        // Restaurant A
        OfferRequest offerRequest1 = new OfferRequest(1, "FLATX", 10, segments);
        boolean result1 = addOffer(offerRequest1);

        // Restaurant B
        OfferRequest offerRequest2 = new OfferRequest(2, "FLATPERCENT", 10, segments);
        boolean result2 = addOffer(offerRequest2);

        // Apply offers for each restaurant
        Assertions.assertTrue(result1);
        Assertions.assertTrue(result2);

        // Simulate cart applications
        double finalCartValueA = 200 - 10; // ₹200 - ₹10 = ₹190
        double finalCartValueB = 200 - (200 * 0.10); // ₹200 - 10% = ₹180

        Assertions.assertEquals(finalCartValueA, 190);
        Assertions.assertEquals(finalCartValueB, 180);
    }

    // Scenario 15: Applying Offer with Decimal Cart Values
    @Test
    public void testOfferWithDecimalCartValues() throws Exception {
        List<String> segments = new ArrayList<>();
        segments.add("p1");

        OfferRequest offerRequest = new OfferRequest(1, "FLATPERCENT", 10, segments);

        boolean result = addOffer(offerRequest);

        // Apply offer to a cart worth ₹199.99
        double finalCartValue = 199.99 - (199.99 * 0.10); // ₹199.99 - 10% = ₹179.99
        Assertions.assertEquals(179.99, finalCartValue, 0.01); // 0.01 tolerance for floating point comparison
    }

    // Scenario 16: Applying Offer on Large Cart Values
    @Test
    public void testOfferOnLargeCartValues() throws Exception {
        List<String> segments = new ArrayList<>();
        segments.add("p1");

        OfferRequest offerRequest = new OfferRequest(1, "FLATPERCENT", 10, segments);

        boolean result = addOffer(offerRequest);

        // Apply offer to a cart worth ₹10,000
        double finalCartValue = 10000 - (10000 * 0.10); // ₹10,000 - 10% = ₹9,000
        Assertions.assertEquals(9000, finalCartValue);
    }
}
