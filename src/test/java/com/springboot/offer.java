public class OfferService {

    public boolean applyOffersForSegment(Segment segment, FlatAmountOfferRequest flatAmountOffer, FlatPercentageOfferRequest flatPercentageOffer) {
        try {
            String urlString = "http://localhost:9001/api/v1/apply-offers";
            URL url = new URL(urlString);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setDoOutput(true);
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");

            ObjectMapper mapper = new ObjectMapper();
            ObjectNode requestBody = mapper.createObjectNode();
            requestBody.putPOJO("segment", segment);
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
        Segment segment = new Segment(/* Populate segment object */);
        FlatAmountOfferRequest flatAmountOffer = new FlatAmountOfferRequest(/* Populate flatAmountOffer object */);
        FlatPercentageOfferRequest flatPercentageOffer = new FlatPercentageOfferRequest(/* Populate flatPercentageOffer object */);

        OfferService offerService = new OfferService();
        boolean success = offerService.applyOffersForSegment(segment, flatAmountOffer, flatPercentageOffer);
        System.out.println("Offers applied successfully: " + success);
    }
}
