package steps;

import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.json.JSONObject;

import java.util.Random;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class PetSteps {
    private JsonPath jsonPath;
    private Response response;
    private int petId;
    private String petName;
    private String petStatus;
    private String updatedName;

    public PetSteps postStoreOrder(String jsonBody) {
        this.jsonPath = given()
                .contentType(ContentType.JSON)
                .body(jsonBody)
                .when()
                .post("https://petstore.swagger.io/v2/store/order")
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .jsonPath();
        return this;
    }

    public PetSteps validateOrderResponse(int expectedId, int expectedPetId, String expectedStatus, boolean expectedComplete) {
        assertThat(jsonPath.getInt("id"), is(expectedId));
        assertThat(jsonPath.getInt("petId"), is(expectedPetId));
        assertThat(jsonPath.getString("status"), is(expectedStatus));
        assertThat(jsonPath.getBoolean("complete"), is(expectedComplete));
        return this;
    }

    public PetSteps updatePet(int petId, String name, String status) {
        this.jsonPath = given()
                .contentType(ContentType.URLENC)
                .formParam("petId", petId)
                .formParam("name", name)
                .formParam("status", status)
                .when()
                .post("https://petstore.swagger.io/v2/pet/" + petId)
                .then()
                .statusCode(200)
                .extract()
                .jsonPath();
        return this;
    }

    public PetSteps validateUpdateResponse(int expectedCode) {
        assertThat(jsonPath.getInt("code"), is(expectedCode));
        assertThat(jsonPath.getString("type"), is(notNullValue()));
        assertThat(jsonPath.getString("message"), is(notNullValue()));
        return this;
    }

    public PetSteps cause404Error(int invalidPetId) {
        this.jsonPath = given()
                .contentType(ContentType.URLENC)
                .formParam("petId", invalidPetId)
                .formParam("name", "NonExistentPet")
                .formParam("status", "available")
                .when()
                .post("https://petstore.swagger.io/v2/pet/" + invalidPetId)
                .then()
                .statusCode(404)
                .extract()
                .jsonPath();
        return this;
    }

    public PetSteps validateErrorResponse(int expectedCode) {
        assertThat(jsonPath.getInt("code"), is(expectedCode));
        assertThat(jsonPath.getString("message"), is(notNullValue()));
        return this;
    }

    public PetSteps createPet() {
        Random rand = new Random();
        this.petName = "Pet" + rand.nextInt(1000);
        this.petId = rand.nextInt(10000);
        this.petStatus = "available";

        JSONObject requestBody = new JSONObject();
        requestBody.put("id", petId);
        requestBody.put("name", petName);
        requestBody.put("status", petStatus);

        this.response = given()
                .contentType(ContentType.JSON)
                .body(requestBody.toString())
                .when()
                .post("https://petstore.swagger.io/v2/pet");

        response.then().statusCode(200);
        return this;
    }

    public PetSteps verifyPetCreated() {
        Response findByStatusResponse = given()
                .queryParam("status", petStatus)
                .when()
                .get("https://petstore.swagger.io/v2/pet/findByStatus");

        findByStatusResponse.then().body("id", hasItem(petId));

        String petNameFromFind = findByStatusResponse.body().jsonPath().getString("find {it.id == " + petId + "}.name");
        assertThat(petNameFromFind, is(equalTo(petName)));
        String petStatusFromFind = findByStatusResponse.body().jsonPath().getString("find {it.id == " + petId + "}.status");
        assertThat(petStatusFromFind, is(equalTo(petStatus)));

        return this;
    }

    public PetSteps updatePet() {
        this.updatedName = "New name" + new Random().nextInt(1000);
        String updatedStatus = "sold";

        JSONObject updateBody = new JSONObject();
        updateBody.put("id", petId);
        updateBody.put("name", updatedName);
        updateBody.put("status", updatedStatus);
        System.out.println("Update Pet Request Body: " + updateBody.toString());

        this.response = given()
                .contentType("application/json")
                .body(updateBody.toString())
                .when()
                .put("https://petstore.swagger.io/v2/pet");

        response.then().statusCode(200);

        System.out.println("Update Pet Response: " + response.body().asString());

        return this;
    }

    public PetSteps verifyPetUpdated() {
        Response getUpdatedPetResponse = given()
                .when()
                .get("https://petstore.swagger.io/v2/pet/" + petId);

        System.out.println("Get Updated Pet Response: " + getUpdatedPetResponse.body().asString());

        getUpdatedPetResponse.then().body("name", equalTo(updatedName));
        getUpdatedPetResponse.then().body("status", equalTo("sold"));

        return this;
    }

}
