package com.tlc.group.seven.orderprocessingservice.order.controller;


import com.tlc.group.seven.orderprocessingservice.order.model.Order;
import com.tlc.group.seven.orderprocessingservice.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/order")
public class OrderController {
    @Autowired
    private OrderService orderService;


//    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody @Valid Order order) {
        return orderService.createOrder(order);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity <?> getOrder(@PathVariable String orderId){
        return orderService.getOrderById(orderId);
    }
    @DeleteMapping("/{orderId}")
    public ResponseEntity<?> cancelOrder(@PathVariable String orderId){
        return orderService.cancelOrder(orderId);
    }

}
