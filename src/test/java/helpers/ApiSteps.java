package helpers;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import model.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ApiSteps {

    @Step("Регистрация пользователя")
    public static RespUser createUser(User user) {
        Response response = ApiRequests.sendPostRequestCreateUser(user);
        response.then().statusCode(200);
        RespUser respUser = response.body().as(RespUser.class);
        assertTrue(respUser.isSuccess());
        return respUser;
    }

    @Step("Авторизация пользователя")
    public static RespUser loginUser(User user) {
        Response response = ApiRequests.sendPostRequestLoginUser(user);
        response.then().statusCode(200);
        RespUser respUser = response.body().as(RespUser.class);
        assertTrue(respUser.isSuccess());
        return respUser;
    }

    @Step("Обновление пользователя")
    public static RespUser updateUser(User user, String accessToken) {
        Response response = ApiRequests.sendPostRequestUpdateUser(user, accessToken);
        response.then().statusCode(200);
        RespUser respUser = response.body().as(RespUser.class);
        assertTrue(respUser.isSuccess());
        return respUser;
    }

    @Step("Удаление пользователя")
    public static boolean deleteUser(String accessToken) {
        Response response = ApiRequests.sendPostRequestDeleteUser(accessToken);
        response.then().statusCode(202);
        Resp resp = response.body().as(Resp.class);
        assertTrue(resp.isSuccess());
        assertEquals("User successfully removed", resp.getMessage());
        return resp.isSuccess();
    }

    @Step("Создание заказа")
    public static RespOrder createOrder(String accessToken, ReqCreateOrder reqCreateOrder) {
        Response response = ApiRequests.sendPostRequestCreateOrder(accessToken, reqCreateOrder);
        response.then().statusCode(200);
        RespOrder respOrder = response.body().as(RespOrder.class);
        assertTrue(respOrder.isSuccess());
        return respOrder;
    }

    @Step("Получение заказов")
    public static RespGetOrders getOrders(String accessToken) {
        Response response = ApiRequests.sendPostRequestGetOrders(accessToken);
        response.then().statusCode(200);
        RespGetOrders respGetOrders = response.body().as(RespGetOrders.class);
        assertTrue(respGetOrders.isSuccess());
        return respGetOrders;
    }
}
