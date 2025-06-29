package model;

import lombok.Data;

@Data
public class RespOrder {
    private String name;
    private Order order;
    private boolean success;
}
