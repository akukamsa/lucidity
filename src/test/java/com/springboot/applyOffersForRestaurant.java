public class OfferService {

    public boolean applyOffersForRestaurant(String restaurantId, FlatAmountOfferRequest flatAmountOffer, FlatPercentageOfferRequest flatPercentageOffer) {
        try {
            String urlString = "http://localhost:9001/api/v1/apply-offer";
            URL url = new URL(urlString);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setDoOutput(true);
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");

            ObjectMapper mapper = new ObjectMapper();
            ObjectNode requestBody = mapper.createObjectNode();
            requestBody.put("restaurantId", restaurantId);
            requestBody.putPOJO("flatAmountOffer", flatAmountOffer);
            requestBody.putPOJO("flatPercentageOffer", flatPercentageOffer);

            String jsonBody = mapper.writeValueAsString(requestBody);

            OutputStream os = con.getOutputStream();
            os.write(jsonBody.getBytes());
            os.flush();
            os.close();

            int responseCode = con.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                System.out.println("Response Body: " + response.toString());
                return true;
            } else if (responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
                System.out.println("Restaurant not found.");
                return false;
            } else {
                System.out.println("POST request failed: " + responseCode);
                return false;
            }
        } catch (Exception e) {
            System.err.println("Exception occurred: " + e.getMessage());
            return false;
        }
    }

    public static void main(String[] args) {
        String restaurantId = "non-existing-id"; // Replace with actual non-existing restaurant ID
        FlatAmountOfferRequest flatAmountOffer = new FlatAmountOfferRequest(/* Populate flatAmountOffer object */);
        FlatPercentageOfferRequest flatPercentageOffer = new FlatPercentageOfferRequest(/* Populate flatPercentageOffer object */);

        OfferService offerService = new OfferService();
        boolean success = offerService.applyOffersForRestaurant(restaurantId, flatAmountOffer, flatPercentageOffer);
        System.out.println("Offers applied successfully: " + success);
    }
}
