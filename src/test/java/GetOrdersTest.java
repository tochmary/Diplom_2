import helpers.ApiRequests;
import helpers.ApiSteps;
import io.qameta.allure.Description;
import io.restassured.response.Response;
import model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GetOrdersTest {

    private static final User USER_1 = new User("mary_test@yandex.ru", "marypass", "Мария");
    private static final ReqCreateOrder ORDER_1 = new ReqCreateOrder(
            List.of("61c0c5a71d1f82001bdaaa6d", "61c0c5a71d1f82001bdaaa6f", "61c0c5a71d1f82001bdaaa73"));
    String accessToken1;

    @Test
    @DisplayName("Получение заказа пользователя")
    @Description("Проверка получения заказа с авторизацией:\n " +
            "1. Код и статус ответа 200 ОК;\n" +
            "2. Ошибок в структуре ответа нет;\n" +
            "3. Заказ показан.")
    public void getOrders() {
        accessToken1 = ApiSteps.createUser(USER_1).getAccessToken();

        RespOrder respOrder = ApiSteps.createOrder(accessToken1, ORDER_1);
        Integer expectedNumberOrder = respOrder.getOrder().getNumber();

        RespGetOrders respGetOrders = ApiSteps.getOrders(accessToken1);

        assertAll("Проверка полей ответа",
                () -> assertEquals(expectedNumberOrder, respGetOrders.getOrders().get(0).getNumber(),
                        "Не заполнено поле orders!"),
                () -> assertNotNull(respGetOrders.getTotal(),
                        "Не заполнено поле total!"),
                () -> assertNotNull(respGetOrders.getTotalToday(),
                        "Не заполнено поле totalToday!")
        );
    }

    @Test
    @DisplayName("Получение пустого списка заказов пользователя")
    @Description("Проверка получения пустого списка заказов пользователя с авторизацией:\n " +
            "1. Код и статус ответа 200 ОК;\n" +
            "2. Ошибок в структуре ответа нет;\n" +
            "3. Заказов нет.")
    public void getOrdersEmpty() {
        accessToken1 = ApiSteps.createUser(USER_1).getAccessToken();

        RespGetOrders respGetOrders = ApiSteps.getOrders(accessToken1);

        assertAll("Проверка полей ответа",
                () -> assertTrue(respGetOrders.getOrders().isEmpty(),
                        "Поле orders не пустое!"),
                () -> assertNotNull(respGetOrders.getTotal(),
                        "Не заполнено поле total!"),
                () -> assertNotNull(respGetOrders.getTotalToday(),
                        "Не заполнено поле totalToday!")
        );
    }

    @Test
    @DisplayName("Получение заказа конкретного пользователя без авторизации")
    @Description("Проверка неуспешного получения заказа без авторизации:\n " +
            "1. Код и статус ответа 401 Unauthorized;\n" +
            "2. В ответе описание ошибки.")
    public void getOrdersWithoutAuth() {
        Response response = ApiRequests.sendPostRequestGetOrders("");
        response.then().statusCode(401);
        Resp resp = response.body().as(Resp.class);

        assertAll("Проверка полей ответа",
                () -> assertFalse(resp.isSuccess(),
                        "Неверное значение поля success!"),
                () -> assertEquals("You should be authorised", resp.getMessage(),
                        "Неверное значение поля message!")
        );
    }

    @AfterEach
    public void tearDown() {
        if (accessToken1 != null) ApiSteps.deleteUser(accessToken1);
    }
}
