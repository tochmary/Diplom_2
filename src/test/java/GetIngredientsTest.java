import helpers.ApiRequests;
import io.qameta.allure.Description;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class GetIngredientsTest {

    @Test
    @DisplayName("Получение данных об ингредиентах") // имя теста
    @Description("Получение данных об ингредиентах")
    void getIngredients() {
        Response response = ApiRequests.sendRequestGetIngredients();
        response.then().statusCode(200);
        assertNotNull(response.getBody(), "Ответ не должен быть пустым!");
    }
}
