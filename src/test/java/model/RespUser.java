package model;

import lombok.Data;

//Тело ответа сервера при успешной регистрации/авторизации
@Data
public class RespUser {
    private boolean success;
    private User user;
    private String accessToken;
    private String refreshToken;
}
