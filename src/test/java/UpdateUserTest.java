import helpers.ApiRequests;
import helpers.ApiSteps;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import model.Resp;
import model.RespUser;
import model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UpdateUserTest {

    private static final User USER_1 = new User(
            "mary_test@yandex.ru", "marypass", "Мария");
    private static final User USER_2 = new User(
            "dary_test@yandex.ru", "darypass", "Дарья");
    String accessToken1;
    String accessToken2;

    @BeforeEach
    public void initEach() {
        accessToken1 = ApiSteps.createUser(USER_1).getAccessToken();
        accessToken2 = ApiSteps.createUser(USER_2).getAccessToken();
        ApiSteps.deleteUser(accessToken2);
    }

    @Test
    @DisplayName("Обновление email авторизованного пользователя")
    @Description("Обновление email авторизованного пользователя:\n " +
            "1. Код и статус ответа 200 ОК;\n" +
            "2. Ошибок в структуре ответа нет;\n" +
            "3. Пользователь обновлен.")
    public void updateUserEmail() {
        User user = new User(USER_1);
        user.setEmail(USER_2.getEmail());
        sendAndCheckCorrectRequestForUpdate(user);
        accessToken1 = ApiSteps.loginUser(user).getAccessToken();
    }

    @Test
    @DisplayName("Обновление пароля авторизованного пользователя")
    @Description("Обновление пароля авторизованного пользователя:\n " +
            "1. Код и статус ответа 200 ОК;\n" +
            "2. Ошибок в структуре ответа нет;\n" +
            "3. Пользователь обновлен.")
    public void updateUserPassword() {
        User user = new User(USER_1);
        user.setPassword(USER_2.getPassword());
        sendAndCheckCorrectRequestForUpdate(user);
        accessToken1 = ApiSteps.loginUser(user).getAccessToken();
    }

    @Test
    @DisplayName("Обновление имя авторизованного пользователя")
    @Description("Обновление имя авторизованного пользователя:\n " +
            "1. Код и статус ответа 200 ОК;\n" +
            "2. Ошибок в структуре ответа нет;\n" +
            "3. Пользователь обновлен.")
    public void updateUserName() {
        User user = new User(USER_1);
        user.setName(USER_2.getName());
        sendAndCheckCorrectRequestForUpdate(user);
    }

    @Test
    @DisplayName("Обновление email неавторизованного пользователя")
    @Description("Проверка неуспешного обновления email неавторизованного пользователя:\n " +
            "1. Код и статус ответа 401 Unauthorized;\n" +
            "2. В ответе описание ошибки.")
    public void updateFailedUserEmailWithoutAuth() {
        User user = new User(USER_1);
        user.setEmail(USER_2.getEmail());
        sendAndCheckIncorrectRequestForUpdate(user);
    }

    @Test
    @DisplayName("Обновление пароля неавторизованного пользователя")
    @Description("Проверка неуспешного обновления пароля неавторизованного пользователя:\n " +
            "1. Код и статус ответа 401 Unauthorized;\n" +
            "2. В ответе описание ошибки.")
    public void updateFailedUserPasswordWithoutAuth() {
        User user = new User(USER_1);
        user.setPassword(USER_2.getPassword());
        sendAndCheckIncorrectRequestForUpdate(user);
    }

    @Test
    @DisplayName("Обновление имя неавторизованного пользователя")
    @Description("Проверка неуспешного обновления имя неавторизованного пользователя:\n " +
            "1. Код и статус ответа 401 Unauthorized;\n" +
            "2. В ответе описание ошибки.")
    public void updateFailedUserNameWithoutAuth() {
        User user = new User(USER_1);
        user.setName(USER_2.getName());
        sendAndCheckIncorrectRequestForUpdate(user);
    }

    @Step("Отправить и проверить корректный запрос на обновление пользователя c авторизацией")
    private void sendAndCheckCorrectRequestForUpdate(User user) {
        RespUser respUser = ApiSteps.updateUser(user, accessToken1);
        assertAll("Проверка полей ответа",
                () -> assertEquals(user.getEmail(), respUser.getUser().getEmail(),
                        "Неверное значение поля email!"),
                () -> assertEquals(user.getName(), respUser.getUser().getName(),
                        "Неверное значение поля name!"),
                () -> assertNull(respUser.getUser().getPassword(),
                        "Заполнено поле password!"),
                () -> assertNull(respUser.getAccessToken(),
                        "Заполнено поле accessToken!"),
                () -> assertNull(respUser.getRefreshToken(),
                        "Заполнено поле refreshToken!")
        );
    }

    @Step("Отправить и проверить некорректный запрос на обновление пользователя без авторизации")
    private static void sendAndCheckIncorrectRequestForUpdate(User user) {
        Response response = ApiRequests.sendPostRequestUpdateUser(user, "");
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
        ApiSteps.deleteUser(accessToken1);
    }
}
