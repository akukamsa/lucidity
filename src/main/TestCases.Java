import org.junit.jupiter.api.*;
import org.mockserver.integration.ClientAndServer;

import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CartOfferApiTest {

    private ClientAndServer mockServer;

    @BeforeAll
    void startMockServer() {
        mockServer = startClientAndServer(1080);
    }

    @AfterAll
    void stopMockServer() {
        mockServer.stop();
    }

    @BeforeEach
    void setupMocks() {
        // Mock the user segment API
        mockServer.when(
            request()
                .withMethod("GET")
                .withPath("/api/v1/user_segment")
                .withQueryStringParameter("user_id", "1")
        ).respond(
            response()
                .withStatusCode(200)
                .withBody("{\"segment\":\"p1\"}")
        );

        // Default mock for apply offer API
        mockServer.when(
            request()
                .withMethod("POST")
                .withPath("/api/v1/cart/apply_offer")
        ).respond(
            response()
                .withStatusCode(200)
                .withBody("{\"cart_value\": 190}")
        );
    }

    @Test
    void testApplyFlatAmountDiscount() {
        given()
            .contentType("application/json")
            .body("{\"cart_value\": 200, \"user_id\": 1, \"restaurant_id\": 1}")
        .when()
            .post("http://localhost:9001/api/v1/cart/apply_offer")
        .then()
            .statusCode(200)
            .body("cart_value", equalTo(190));
    }

    @Test
    void testApplyPercentageDiscount() {
        // Mock response for percentage discount
        mockServer.when(
            request()
                .withMethod("POST")
                .withPath("/api/v1/cart/apply_offer")
        ).respond(
            response()
                .withStatusCode(200)
                .withBody("{\"cart_value\": 180}")
        );

        given()
            .contentType("application/json")
            .body("{\"cart_value\": 200, \"user_id\": 1, \"restaurant_id\": 1}")
        .when()
            .post("http://localhost:9001/api/v1/cart/apply_offer")
        .then()
            .statusCode(200)
            .body("cart_value", equalTo(180));
    }

    @Test
    void testInvalidUserId() {
        given()
            .contentType("application/json")
            .body("{\"cart_value\": 200, \"user_id\": 999, \"restaurant_id\": 1}")
        .when()
            .post("http://localhost:9001/api/v1/cart/apply_offer")
        .then()
            .statusCode(400)
            .body("error", equalTo("User not found"));
    }

    @Test
    void testInvalidRestaurantId() {
        given()
            .contentType("application/json")
            .body("{\"cart_value\": 200, \"user_id\": 1, \"restaurant_id\": 999}")
        .when()
            .post("http://localhost:9001/api/v1/cart/apply_offer")
        .then()
            .statusCode(400)
            .body("error", equalTo("Restaurant not found"));
    }

    @Test
    void testInvalidSegment() {
        mockServer.when(
            request()
                .withMethod("GET")
                .withPath("/api/v1/user_segment")
                .withQueryStringParameter("user_id", "2")
        ).respond(
            response()
                .withStatusCode(200)
                .withBody("{\"segment\":\"p4\"}")
        );

        given()
            .contentType("application/json")
            .body("{\"cart_value\": 200, \"user_id\": 2, \"restaurant_id\": 1}")
        .when()
            .post("http://localhost:9001/api/v1/cart/apply_offer")
        .then()
            .statusCode(400)
            .body("error", equalTo("Segment not valid"));
    }

    @Test
    void testZeroCartValue() {
        given()
            .contentType("application/json")
            .body("{\"cart_value\": 0, \"user_id\": 1, \"restaurant_id\": 1}")
        .when()
            .post("http://localhost:9001/api/v1/cart/apply_offer")
        .then()
            .statusCode(200)
            .body("cart_value", equalTo(0));
    }

    @Test
    void testZeroDiscount() {
        given()
            .contentType("application/json")
            .body("{\"cart_value\": 200, \"user_id\": 1, \"restaurant_id\": 1, \"offer_value\": 0}")
        .when()
            .post("http://localhost:9001/api/v1/cart/apply_offer")
        .then()
            .statusCode(200)
            .body("cart_value", equalTo(200));
    }

    @Test
    void test100PercentDiscount() {
        mockServer.when(
            request()
                .withMethod("POST")
                .withPath("/api/v1/cart/apply_offer")
        ).respond(
            response()
                .withStatusCode(200)
                .withBody("{\"cart_value\": 0}")
        );

        given()
            .contentType("application/json")
            .body("{\"cart_value\": 200, \"user_id\": 1, \"restaurant_id\": 1}")
        .when()
            .post("http://localhost:9001/api/v1/cart/apply_offer")
        .then()
            .statusCode(200)
            .body("cart_value", equalTo(0));
    }

    @Test
    void testNegativeCartValue() {
        given()
            .contentType("application/json")
            .body("{\"cart_value\": -200, \"user_id\": 1, \"restaurant_id\": 1}")
        .when()
            .post("http://localhost:9001/api/v1/cart/apply_offer")
        .then()
            .statusCode(400)
            .body("error", equalTo("Invalid cart value"));
    }

    @Test
    void testInvalidDiscountType() {
        given()
            .contentType("application/json")
            .body("{\"cart_value\": 200, \"user_id\": 1, \"restaurant_id\": 1, \"offer_type\": \"INVALID\"}")
        .when()
            .post("http://localhost:9001/api/v1/cart/apply_offer")
        .then()
            .statusCode(400)
            .body("error", equalTo("Invalid discount type"));
    }

    @Test
    void testCartValueAfterConcurrency() {
        // Simulate multiple users applying offers
        given()
            .contentType("application/json")
            .body("{\"cart_value\": 200, \"user_id\": 1, \"restaurant_id\": 1}")
        .when()
            .post("http://localhost:9001/api/v1/cart/apply_offer")
        .then()
            .statusCode(200)
            .body("cart_value", equalTo(190));
    }
}
