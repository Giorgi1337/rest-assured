package steps;

import io.restassured.path.json.JsonPath;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class BookSteps {

    private JsonPath jsonPath;

    public BookSteps fetchBooks() {
        this.jsonPath = given()
                .when()
                .get("/BookStore/v1/Books")
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .jsonPath();
        return this;
    }

    public BookSteps validateBookISBN(int bookIndex, String expectedISBN) {
        String actualISBN = jsonPath.getString("books[" + bookIndex + "].isbn");
        assertThat(actualISBN, is(expectedISBN));
        return this;
    }

    public BookSteps validateBookAuthor(int bookIndex, String expectedAuthor) {
        String actualAuthor = jsonPath.getString("books[" + bookIndex + "].author");
        assertThat(actualAuthor, is(expectedAuthor));
        return this;
    }

    public BookSteps fetchBookByISBN(String isbn) {
        this.jsonPath = given()
                .when()
                .get("/BookStore/v1/Book?ISBN=" + isbn)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .jsonPath();
        return this;
    }

    public BookSteps validateBookDetails(String expectedISBN, String expectedAuthor) {
        String isbn = jsonPath.getString("isbn");
        String author = jsonPath.getString("author");
        String title = jsonPath.getString("title");
        String publishDate = jsonPath.getString("publish_date");
        int pages = jsonPath.getInt("pages");

        assertThat(isbn, is(expectedISBN));
        assertThat(author, is(expectedAuthor));
        assertThat(title, is(notNullValue()));
        assertThat(publishDate, is(notNullValue()));
        assertThat(pages, greaterThan(0));

        return this;
    }

    public BookSteps logBookDetails() {
        String author = jsonPath.getString("author");
        String title = jsonPath.getString("title");
        String publishDate = jsonPath.getString("publish_date");

        System.out.printf("Author: %s, Title: %s, Publish Date: %s%n", author, title, publishDate);
        return this;
    }

    public BookSteps deleteBookUnauthorized() {
        this.jsonPath = given()
                .when()
                .delete("BookStore/v1/Book")
                .then()
                .assertThat()
                .statusCode(401)
                .extract()
                .jsonPath();
        return this;
    }

    public BookSteps validateUnauthorizedMessage(String expectedMessage) {
        String actualMessage = jsonPath.getString("message");
        assertThat(actualMessage, is(expectedMessage));
        return this;
    }

    public BookSteps validateBookPagesLessThan1000() {
        jsonPath.getList("books.pages", Integer.class)
                .forEach(pages -> assertThat(pages, lessThan(1000)));
        return this;
    }

}
