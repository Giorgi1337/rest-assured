package rest;

import data.BookDataProvider;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.testng.annotations.Test;

import java.io.File;

import static data.Constants.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class BooksApiTest extends BaseTest {

    @Test
    public void getBooks() {
        bookSteps
                .fetchBooks()
                .validateBookISBN(0, EXPECTED_ISBN_1)
                .validateBookAuthor(0, EXPECTED_AUTHOR_1)
                .validateBookISBN(1, EXPECTED_ISBN_2)
                .validateBookAuthor(1, EXPECTED_AUTHOR_2);
    }


    @Test(dataProvider = "bookData", dataProviderClass = BookDataProvider.class)
    public void getBookDetails(String isbn, String expectedAuthor) {
        bookSteps
                .fetchBookByISBN(isbn)
                .validateBookDetails(isbn, expectedAuthor)
                .logBookDetails();
    }

    @Test
    public void deleteBookUnauthorized() {
        bookSteps
                .deleteBookUnauthorized()
                .validateUnauthorizedMessage(UNAUTHORIZED_MSG);
    }

    @Test
    public void searchHarryPotterBooks() {
        bookSearchSteps
                .searchBooks(HARRY_POTTER)
                .validateFirstBookTitle(FIRST_BOOK_TITLE)
                .validateFirstBookAuthor(FIRST_BOOK_AUTHOR)
                .validateFirstBookHasKey("title")
                .validateFirstBookHasKey("author_name")
                .validateFirstBookPlaces();
    }

    @Test
    public void validateBooks() {
        bookSteps
                .fetchBooks()
                .validateBookPagesLessThan1000()
                .validateBookAuthor(0, EXPECTED_AUTHOR_1)
                .validateBookAuthor(1, EXPECTED_AUTHOR_2);
    }


    @Test
    public void bookingUpdateTest() {
        RestAssured.baseURI = "https://restful-booker.herokuapp.com";

        // Generate Token (default)
        JSONObject authRequest = new JSONObject();
        authRequest.put("username", "admin");
        authRequest.put("password", "password123");

        JsonPath authJson = given()
                .header("Content-Type", "application/json")
                .body(authRequest.toString())
                .when()
                .post("/auth")
                .then()
                .statusCode(200)
                .body("token", notNullValue())
                .extract()
                .jsonPath();

        String token = authJson.getString("token");

        // Booking ID
        JsonPath bookingIdsJson = given()
                .when()
                .get("/booking")
                .then()
                .statusCode(200)
                .body("$", not(empty()))
                .extract()
                .jsonPath();

        int bookingId = bookingIdsJson.getInt("[0].bookingid");

        // Update Booking
        JSONObject requestBody = new JSONObject();
        requestBody.put("firstname", "Rosko");
        requestBody.put("lastname", "Rosko 123");
        requestBody.put("totalprice", 157);
        requestBody.put("depositpaid", true);

        JSONObject bookingDates = new JSONObject();
        bookingDates.put("checkin", "2025-01-20");
        bookingDates.put("checkout", "2025-01-25");
        requestBody.put("bookingdates", bookingDates);
        requestBody.put("additionalneeds", "Breakfast");

        JsonPath updateJson = given()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Cookie", "token=" + token)
                .body(requestBody.toString())
                .when()
                .put("/booking/" + bookingId)
                .then()
                .statusCode(200)
                .extract()
                .jsonPath();

        assertThat(updateJson.getString("firstname"), equalTo("Rosko"));
        assertThat(updateJson.getString("lastname"), equalTo("Rosko 123"));
        assertThat(updateJson.getInt("totalprice"), equalTo(157));
        assertThat(updateJson.getBoolean("depositpaid"), is(true));
        assertThat(updateJson.getString("additionalneeds"), equalTo("Breakfast"));
        assertThat(updateJson.getString("bookingdates.checkin"), equalTo("2025-01-20"));
        assertThat(updateJson.getString("bookingdates.checkout"), equalTo("2025-01-25"));
    }

    @Test
    public void uploadImageTest() {
        RestAssured.baseURI = "https://petstore.swagger.io/v2";

        int petId = 12345;
        String additionalMetadata = "Image upload";
        File file = new File("src/main/resources/200.jpg");

        long expectedFileSize = file.length();

        Response response = given()
                .header("Content-Type", "multipart/form-data")
                .multiPart("additionalMetadata", additionalMetadata)
                .multiPart("file", file)
                .pathParam("petId", petId)
                .when()
                .post("/pet/{petId}/uploadImage")
                .then()
                .statusCode(200)
                .extract()
                .response();

        String message = response.jsonPath().getString("message");

        // Validate additionalMetadata and filename
        assertThat(message, containsString(additionalMetadata));
        assertThat(message, containsString(file.getName()));

        // Extract file size from message and validate
        String fileSizePart = message.split(",")[1].trim();
        long actualFileSize = Long.parseLong(fileSizePart.split(" ")[0]);
        assertThat(actualFileSize, equalTo(expectedFileSize));
    }

}
