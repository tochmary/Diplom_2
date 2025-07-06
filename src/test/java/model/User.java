package model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class User {
    private String email;
    private String password;
    private String name;

    public User(User user) {
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.name = user.getName();
    }
}
