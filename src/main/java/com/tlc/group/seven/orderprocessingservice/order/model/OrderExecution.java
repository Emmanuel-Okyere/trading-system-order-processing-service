package com.tlc.group.seven.orderprocessingservice.order.model;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

@Data
public class OrderExecution {
    private String product;
    @NotNull
    private Integer quantity;
    @NotNull
    private Double price;

    @Size(min = 2)
    private String orderType;
    @Size (min = 3)
    private String side;
    private Integer commulativeQuantity;
    private Double cumulatitivePrice;
    @Column
    private String orderID;
    private List<Execution> executions;
    @CreationTimestamp
    private Date createdAt;
    @UpdateTimestamp
    private Date updatedAt;
}
