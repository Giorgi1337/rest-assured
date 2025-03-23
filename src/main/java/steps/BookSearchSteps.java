package steps;

import io.restassured.path.json.JsonPath;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.is;

public class BookSearchSteps {

    private JsonPath jsonPath;

    public BookSearchSteps searchBooks(String keyword) {
        this.jsonPath = given()
                .queryParam("q", keyword)
                .when()
                .get("https://openlibrary.org/search.json")
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .jsonPath();
        return this;
    }

    public BookSearchSteps validateFirstBookTitle(String expectedTitle) {
        String firstBookTitle = jsonPath.getString("docs[0].title");
        assertThat(firstBookTitle, is(expectedTitle));
        return this;
    }

    public BookSearchSteps validateFirstBookAuthor(String expectedAuthor) {
        String firstBookAuthor = jsonPath.getString("docs[0].author_name[0]");
        assertThat(firstBookAuthor, is(expectedAuthor));
        return this;
    }

    public BookSearchSteps validateFirstBookHasKey(String key) {
        assertThat(jsonPath.get("docs[0]"), hasKey(key));
        return this;
    }

    public BookSearchSteps validateFirstBookPlaces() {
        assertThat(jsonPath.get("docs[0]"), hasKey("place"));
        return this;
    }
}
