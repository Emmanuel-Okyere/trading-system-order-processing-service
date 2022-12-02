package com.tlc.group.seven.orderprocessingservice.order.model;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class Execution {
    private Date timestamp;
    @NotNull
    private Double price;
    @NotNull
    private int quantity;


}
