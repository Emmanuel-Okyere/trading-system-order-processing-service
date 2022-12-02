package com.tlc.group.seven.orderprocessingservice.order.controller;

import com.tlc.group.seven.orderprocessingservice.constant.ServiceConstants;
import com.tlc.group.seven.orderprocessingservice.order.model.Order;
import com.tlc.group.seven.orderprocessingservice.order.payload.ErrorResponse;
import com.tlc.group.seven.orderprocessingservice.order.payload.OrderResponse;
import com.tlc.group.seven.orderprocessingservice.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

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

    @GetMapping
    public ResponseEntity<?> getAllOrderByAUser(){
        return orderService.getAllOrdersByUser();
    }
}
