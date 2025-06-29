import helpers.ApiRequests;
import helpers.ApiSteps;
import io.qameta.allure.Description;
import io.restassured.response.Response;
import model.Resp;
import model.RespUser;
import model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class LoginUserTest {

    private static final User USER_1 = new User(
            "mary_test@yandex.ru", "marypass", "Мария");
    private static final User USER_2 = new User(
            "dary_test@yandex.ru", "darypass", "Дарья");
    String accessToken1;
    String accessToken2;
    RespUser respUser1;

    @BeforeEach
    public void initEach() {
        respUser1 = ApiSteps.createUser(USER_1);
        accessToken1 = respUser1.getAccessToken();
        accessToken2 = ApiSteps.createUser(USER_2).getAccessToken();
        ApiSteps.deleteUser(accessToken2);
    }

    @Test
    @DisplayName("Авторизация пользователя")
    @Description("Проверка авторизации существующего пользователя:\n " +
            "1. Код и статус ответа 200 ОК;\n" +
            "2. Ошибок в структуре ответа нет.")
    public void loginUser() {
        User user = new User(USER_1);
        user.setName(null);
        RespUser respUser2 = ApiSteps.loginUser(user);
        assertAll("Проверка полей ответа",
                () -> assertEquals(respUser1.getUser().getEmail(), respUser2.getUser().getEmail(),
                        "Неверное значение поля email!"),
                () -> assertEquals(respUser1.getUser().getName(), respUser2.getUser().getName(),
                        "Неверное значение поля name!"),
                () -> assertNull(respUser2.getUser().getPassword(),
                        "Заполнено поле password!"),
                () -> assertNotNull(respUser2.getAccessToken(), //TODO почему другой у авторизации?
                        "Не заполнено поле accessToken!"),
                () -> assertNotNull(respUser2.getRefreshToken(), //TODO почему другой у авторизации?
                        "Не заполнено поле refreshToken!")
        );
    }

    @Test
    @DisplayName("Авторизация пользователя c неверным логином")
    @Description("Проверка неуспешной авторизации пользователя c неверным логином:\n " +
            "1. Код и статус ответа 401 Unauthorized;\n" +
            "2. В ответе описание ошибки.")
    public void loginFailedUserWithWrongLogin() {
        User user = new User(USER_2.getEmail(), USER_1.getPassword(), null);
        Response response = ApiRequests.sendPostRequestLoginUser(user);
        response.then().statusCode(401);
        Resp resp = response.body().as(Resp.class);
        assertAll("Проверка полей ответа",
                () -> assertFalse(resp.isSuccess(),
                        "Неверное значение поля success!"),
                () -> assertEquals("email or password are incorrect", resp.getMessage(),
                        "Неверное значение поля message!")
        );
    }

    @Test
    @DisplayName("Авторизация пользователя c неверным паролем")
    @Description("Проверка неуспешной авторизации пользователя c неверным паролем:\n " +
            "1. Код и статус ответа 401 Unauthorized;\n" +
            "2. В ответе описание ошибки.")
    public void loginFailedUserWithWrongPassword() {
        User user = new User(USER_1.getEmail(), USER_2.getPassword(), null);
        Response response = ApiRequests.sendPostRequestLoginUser(user);
        response.then().statusCode(401);
        Resp resp = response.body().as(Resp.class);
        assertAll("Проверка полей ответа",
                () -> assertFalse(resp.isSuccess(),
                        "Неверное значение поля success!"),
                () -> assertEquals("email or password are incorrect", resp.getMessage(),
                        "Неверное значение поля message!")
        );
    }

    @AfterEach
    public void tearDown() {
        ApiSteps.deleteUser(accessToken1);
    }
}
