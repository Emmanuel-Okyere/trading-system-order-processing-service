package com.tlc.group.seven.orderprocessingservice.order.model;

import lombok.Data;

import java.util.Date;

@Data
public class Execution {
    private Date timestamp;
    private Double price;
    private Integer quantity;
}
