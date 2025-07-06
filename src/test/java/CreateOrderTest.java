import helpers.ApiRequests;
import helpers.ApiSteps;
import io.qameta.allure.Description;
import io.restassured.response.Response;
import model.ReqCreateOrder;
import model.Resp;
import model.RespOrder;
import model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CreateOrderTest {

    private static final User USER_1 = new User(
            "mary_test@yandex.ru", "marypass", "Мария");
    private static final ReqCreateOrder ORDER_1 = new ReqCreateOrder(
            List.of("61c0c5a71d1f82001bdaaa6d", "61c0c5a71d1f82001bdaaa6f", "61c0c5a71d1f82001bdaaa73"));
    private static final ReqCreateOrder ORDER_EMPTY = new ReqCreateOrder(List.of());
    private static final ReqCreateOrder ORDER_WRONG = new ReqCreateOrder(
            List.of("1", "61c0c5a71d1f82001bdaaa6f", "61c0c5a71d1f82001bdaaa73"));
    String accessToken1;

    @Test
    @DisplayName("Создание заказа с авторизацией")
    @Description("Проверка создания заказа с авторизацией:\n " +
            "1. Код и статус ответа 200 ОК;\n" +
            "2. Ошибок в структуре ответа нет.")
    public void createOrder() {
        accessToken1 = ApiSteps.createUser(USER_1).getAccessToken();

        RespOrder respOrder = ApiSteps.createOrder(accessToken1, ORDER_1);
        assertAll("Проверка полей ответа",
                () -> assertNotNull(respOrder.getName(),
                        "Не заполнено поле name!"),
                () -> assertNotNull(respOrder.getOrder().getNumber(),
                        "Не заполнено поле order.number!"),
                () -> assertEquals(USER_1.getName(), respOrder.getOrder().getOwner().getName(),
                        "Неверное значение поля order.owner.name!"),
                () -> assertEquals(USER_1.getEmail(), respOrder.getOrder().getOwner().getEmail(),
                        "Неверное значение поля order.owner.email!")
        );
    }

    @Test
    @DisplayName("Создание заказа с авторизацией без ингредиентов")
    @Description("Проверка неуспешного создания заказа с авторизацией без ингредиентов:\n " +
            "1. Код и статус ответа 400 Bad Request;\n" +
            "2. В ответе описание ошибки.")
    public void createFailedWithoutIngredients() {
        accessToken1 = ApiSteps.createUser(USER_1).getAccessToken();

        Response response = ApiRequests.sendPostRequestCreateOrder(accessToken1, ORDER_EMPTY);
        response.then().statusCode(400);
        Resp resp = response.body().as(Resp.class);
        assertAll("Проверка полей ответа",
                () -> assertFalse(resp.isSuccess(),
                        "Неверное значение поля success!"),
                () -> assertEquals("Ingredient ids must be provided", resp.getMessage(),
                        "Неверное значение поля message!")
        );
    }

    @Test
    @DisplayName("Создание заказа с авторизацией и неверным хешем ингредиентов")
    @Description("Проверка неуспешного создания заказа с авторизацией и с неверным хешем ингредиентов:\n " +
            "1. Код и статус ответа 500 Internal Server Error.")
    public void createFailedWithWrongIngredient() {
        accessToken1 = ApiSteps.createUser(USER_1).getAccessToken();

        Response response = ApiRequests.sendPostRequestCreateOrder(accessToken1, ORDER_WRONG);
        response.then().statusCode(500);
    }

    @Test
    @DisplayName("Создание заказа без авторизации")
    @Description("Проверка создания заказа без авторизации:\n " +
            "1. Код и статус ответа 200 ОК;\n" +
            "2. Ошибок в структуре ответа нет.")
    public void createOrderWithoutAuth() {
        RespOrder respOrder = ApiSteps.createOrder("", ORDER_1);
        assertAll("Проверка полей ответа",
                () -> assertNotNull(respOrder.getName(),
                        "Не заполнено поле name!"),
                () -> assertNotNull(respOrder.getOrder().getNumber(),
                        "Не заполнено поле order.number!"),
                () -> assertNull(respOrder.getOrder().getOwner(),
                        "Заполнено поле order.owner!")
        );
    }

    @Test
    @DisplayName("Создание заказа без авторизации и без ингредиентов")
    @Description("Проверка неуспешного создания заказа без авторизации и без ингредиентов:\n " +
            "1. Код и статус ответа 400 Bad Request;\n" +
            "2. В ответе описание ошибки.")
    public void createFailedWithoutAuthWithoutIngredients() {
        Response response = ApiRequests.sendPostRequestCreateOrder("", ORDER_EMPTY);
        response.then().statusCode(400);
        Resp resp = response.body().as(Resp.class);
        assertAll("Проверка полей ответа",
                () -> assertFalse(resp.isSuccess(),
                        "Неверное значение поля success!"),
                () -> assertEquals("Ingredient ids must be provided", resp.getMessage(),
                        "Неверное значение поля message!")
        );
    }

    @Test
    @DisplayName("Создание заказа без авторизации с неверным хешем ингредиентов")
    @Description("Проверка неуспешного создания заказа без авторизации с неверным хешем ингредиентов:\n " +
            "1. Код и статус ответа 500 Internal Server Error.")
    public void createFailedWithoutAuthWithWrongIngredient() {
        Response response = ApiRequests.sendPostRequestCreateOrder("", ORDER_WRONG);
        response.then().statusCode(500);
    }

    @AfterEach
    public void tearDown() {
        if (accessToken1 != null) ApiSteps.deleteUser(accessToken1);
    }
}
