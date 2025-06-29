import helpers.ApiRequests;
import helpers.ApiSteps;
import io.qameta.allure.Description;
import io.restassured.response.Response;
import model.Resp;
import model.RespUser;
import model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CreateUserTest {

    private static final User USER_1 = new User("mary_test@yandex.ru", "marypass", "Мария");
    String accessToken1;

    @Test
    @DisplayName("Регистрация пользователя")
    @Description("Проверка создания пользователя с корректными данными:\n " +
            "1. Код и статус ответа 200 ОК;\n" +
            "2. Ошибок в структуре ответа нет.")
    public void createUser() {
        RespUser respUser = ApiSteps.createUser(USER_1);
        assertAll("Проверка полей ответа",
                () -> assertEquals(USER_1.getEmail(), respUser.getUser().getEmail(),
                        "Неверное значение поля email!"),
                () -> assertEquals(USER_1.getName(), respUser.getUser().getName(),
                        "Неверное значение поля name!"),
                () -> assertNull(respUser.getUser().getPassword(),
                        "Заполнено поле password!"),
                () -> assertNotNull(respUser.getAccessToken(),
                        "Не заполнено поле accessToken!"),
                () -> assertNotNull(respUser.getRefreshToken(),
                        "Не заполнено поле refreshToken!")
        );
        accessToken1 = respUser.getAccessToken();
    }

    @Test
    @DisplayName("Регистрация пользователя, который уже зарегистрирован")
    @Description("Проверка неуспешного создания пользователя, который уже зарегистрирован:\n " +
            "1. Код и статус ответа 403 Forbidden;\n" +
            "2. В ответе описание ошибки.")
    public void createFailedExistUser() {
        accessToken1 = ApiSteps.createUser(USER_1).getAccessToken();

        Response response = ApiRequests.sendPostRequestCreateUser(USER_1);
        response.then().statusCode(403);
        Resp resp = response.body().as(Resp.class);
        assertAll("Проверка полей ответа",
                () -> assertFalse(resp.isSuccess(),
                        "Неверное значение поля success!"),
                () -> assertEquals("User already exists", resp.getMessage(),
                        "Неверное значение поля message!")
        );
    }

    @Test
    @DisplayName("Регистрация пользователя с незаполненным полем email")
    @Description("Проверка неуспешного создания пользователя с незаполненным полем email:\n " +
            "1. Код и статус ответа 403 Forbidden;\n" +
            "2. В ответе описание ошибки.")
    public void createFailedUserWithoutEmail() {
        User user = new User(USER_1);
        user.setEmail(null);
        Response response = ApiRequests.sendPostRequestCreateUser(user);
        response.then().statusCode(403);
        Resp resp = response.body().as(Resp.class);
        assertAll("Проверка полей ответа",
                () -> assertFalse(resp.isSuccess(),
                        "Неверное значение поля success!"),
                () -> assertEquals("Email, password and name are required fields", resp.getMessage(),
                        "Неверное значение поля message!")
        );
    }

    @Test
    @DisplayName("Регистрация пользователя с незаполненным полем name")
    @Description("Проверка неуспешного создания пользователя с незаполненным полем name:\n " +
            "1. Код и статус ответа 403 Forbidden;\n" +
            "2. В ответе описание ошибки.")
    public void createFailedUserWithoutName() {
        User user = new User(USER_1);
        user.setName(null);
        Response response = ApiRequests.sendPostRequestCreateUser(user);
        response.then().statusCode(403);
        Resp resp = response.body().as(Resp.class);
        assertAll("Проверка полей ответа",
                () -> assertFalse(resp.isSuccess(),
                        "Неверное значение поля success!"),
                () -> assertEquals("Email, password and name are required fields", resp.getMessage(),
                        "Неверное значение поля message!")
        );
    }

    @Test
    @DisplayName("Регистрация пользователя с незаполненным полем password")
    @Description("Проверка неуспешного создания пользователя с незаполненным полем password:\n " +
            "1. Код и статус ответа 403 Forbidden;\n" +
            "2. В ответе описание ошибки.")
    public void createFailedUserWithoutPassword() {
        User user = new User(USER_1);
        user.setPassword(null);
        Response response = ApiRequests.sendPostRequestCreateUser(user);
        response.then().statusCode(403);
        Resp resp = response.body().as(Resp.class);
        assertAll("Проверка полей ответа",
                () -> assertFalse(resp.isSuccess(),
                        "Неверное значение поля success!"),
                () -> assertEquals("Email, password and name are required fields", resp.getMessage(),
                        "Неверное значение поля message!")
        );
    }

    @AfterEach
    public void tearDown() {
        if (accessToken1 != null) ApiSteps.deleteUser(accessToken1);
    }
}
