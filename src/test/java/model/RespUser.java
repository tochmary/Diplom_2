package model;

import lombok.Data;

@Data
public class RespUser {
    private boolean success;
    private User user;
    private String accessToken;
    private String refreshToken;
}
