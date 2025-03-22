package openapi;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.ErrorLoggingFilter;
import io.restassured.filter.log.LogDetail;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;
import pet.store.v3.invoker.ApiClient;
import pet.store.v3.invoker.JacksonObjectMapper;
import pet.store.v3.model.Category;
import pet.store.v3.model.Order;
import pet.store.v3.model.Pet;
import pet.store.v3.model.OrderAssert;
import pet.store.v3.model.PetAssert;

import java.time.OffsetDateTime;

import static io.restassured.RestAssured.config;
import static io.restassured.config.ObjectMapperConfig.objectMapperConfig;
import static pet.store.v3.invoker.ResponseSpecBuilders.shouldBeCode;
import static pet.store.v3.invoker.ResponseSpecBuilders.validatedWith;

public class PetTest {
    private ApiClient api;

    @BeforeSuite
    public void setUpSuite() {
        api = ApiClient.api(ApiClient.Config.apiConfig()
                .reqSpecSupplier(() -> new RequestSpecBuilder()
                        .setBaseUri("https://petstore3.swagger.io/api/v3")
                        .setConfig(config()
                                .objectMapperConfig(objectMapperConfig()
                                        .defaultObjectMapper(JacksonObjectMapper.jackson())))
                        .addFilter(new AllureRestAssured())
                        .addFilter(new ErrorLoggingFilter())
                        .log(LogDetail.ALL)));
    }

    @Test
    public void createOrderTest() {
        Order order = new Order()
                .id(123L)
                .petId(5678L)
                .quantity(1)
                .shipDate(OffsetDateTime.now())
                .status(Order.StatusEnum.PLACED)
                .complete(true);

        Order createdOrder = api.store().placeOrder().body(order)
                .executeAs(validatedWith(shouldBeCode(200)));

        // Using the generated assertion class for Order
        OrderAssert.assertThat(createdOrder)
                .hasId(123L)
                .hasStatus(Order.StatusEnum.PLACED)
                .hasComplete(true);
    }

    @Test
    public void createPetTest() {
        Pet pet = new Pet()
                .id(101L)
                .name("Tuzik")
                .status(Pet.StatusEnum.AVAILABLE)
                .category(new Category().id(1L).name("Dogs"));

        Pet createdPet = api.pet().addPet().body(pet).executeAs(validatedWith(shouldBeCode(200)));

        // Using the generated assertion class for Pet
        PetAssert.assertThat(createdPet)
                .hasId(101L)
                .hasName("Tuzik")
                .hasStatus(Pet.StatusEnum.AVAILABLE)
                .hasCategory(new Category().id(1L).name("Dogs"));
    }
}
