package rest;

import io.restassured.RestAssured;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import steps.BookSearchSteps;
import steps.BookSteps;
import steps.PetSteps;
import steps.UserSteps;

public class BaseTest {
    protected BookSteps bookSteps;
    protected PetSteps petSteps;
    protected UserSteps userSteps;
    protected BookSearchSteps bookSearchSteps;

    @BeforeClass
    public void setUp() {
        bookSteps = new BookSteps();
        petSteps = new PetSteps();
        userSteps = new UserSteps();
        bookSearchSteps = new BookSearchSteps();
    }

    @BeforeMethod
    public void setUpBefore() {
        RestAssured.baseURI = "https://bookstore.toolsqa.com";
    }

}
