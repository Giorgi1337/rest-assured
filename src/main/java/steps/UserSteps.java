package steps;

import io.restassured.path.json.JsonPath;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;

public class UserSteps {
    private JsonPath response;

    public UserSteps userLogin(String username, String password) {
        this.response = given()
                .queryParam("username", username)
                .queryParam("password", password)
                .when()
                .get("https://petstore.swagger.io/v2/user/login")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath();
        return this;
    }

    public UserSteps validateSessionId() {
        String sessionId = response.getString("message").split(":")[1].trim();
        assertThat(sessionId.matches("\\d+"), is(true));
        assertThat(sessionId.length(), greaterThanOrEqualTo(10));
        return this;
    }
}
