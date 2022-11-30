package com.tlc.group.seven.orderprocessingservice.order.controller;

import com.tlc.group.seven.orderprocessingservice.order.model.Order;
import com.tlc.group.seven.orderprocessingservice.order.payload.OrderResponse;
import com.tlc.group.seven.orderprocessingservice.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

//    @Autowired
//    public OrderController(OrderService orderService) {
//        this.orderService = orderService;
//    }

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody Order order){
        OrderResponse orderResponse = orderService.createOrder(order);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderResponse);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity <?> getOrder(@PathVariable String orderId){
        OrderResponse orderResponse = orderService.getOrderById(orderId);
        return ResponseEntity.status(HttpStatus.OK).body(orderResponse);
    }
}
