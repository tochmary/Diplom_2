package model;

import lombok.Data;

import java.util.List;

@Data
public class RespGetOrders {
    private boolean success;
    private List<Order> orders;
    private Integer total;
    private Integer totalToday;
}
