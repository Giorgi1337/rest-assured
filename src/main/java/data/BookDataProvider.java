package data;

import org.testng.annotations.DataProvider;

public class BookDataProvider {

    @DataProvider(name = "bookData")
    public static Object[][] getBookData() {
        return new Object[][] {
                {"9781449325862", "Richard E. Silverman"},
                {"9781449331818", "Addy Osmani"},
                {"9781449337711", "Glenn Block et al."},
                {"9781449365035", "Axel Rauschmayer"},
                {"9781491904244", "Kyle Simpson"},
                {"9781491950296", "Eric Elliott"},
                {"9781593275846", "Marijn Haverbeke"},
                {"9781593277574", "Nicholas C. Zakas"}
        };
    }

}
