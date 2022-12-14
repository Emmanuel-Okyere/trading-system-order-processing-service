package com.tlc.group.seven.orderprocessingservice.order.payload;


import lombok.Data;


@Data
public class OrderResponse {
    private Long id;
    private String orderId;
    private int quantity;
    private String product;
    private double price;
    private String type;
    private String orderStatus;

    public OrderResponse( Long id, String orderId, int quantity, String product, double price, String type, String orderStatus) {
        this.id = id;
        this.orderId = orderId;
        this.quantity = quantity;
        this.product = product;
        this.price = price;
        this.type = type;
        this.orderStatus = orderStatus;
    }
}
