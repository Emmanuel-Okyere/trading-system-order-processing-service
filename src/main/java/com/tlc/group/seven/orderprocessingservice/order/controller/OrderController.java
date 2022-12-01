package com.tlc.group.seven.orderprocessingservice.order.controller;

import com.tlc.group.seven.orderprocessingservice.constant.ServiceConstants;
import com.tlc.group.seven.orderprocessingservice.order.model.Order;
import com.tlc.group.seven.orderprocessingservice.order.payload.ErrorResponse;
import com.tlc.group.seven.orderprocessingservice.order.payload.OrderResponse;
import com.tlc.group.seven.orderprocessingservice.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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
    public ResponseEntity<?> createOrder(@RequestBody @Valid Order order) {
        OrderResponse orderResponse = orderService.createOrder(order);
        if (orderResponse != null) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(orderResponse);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.builder()
                        .status(ServiceConstants.failureStatus)
                        .message(ServiceConstants.UnsuccessfullOrderCreation).build());
    }
    @GetMapping("/{orderId}")
    public ResponseEntity <?> getOrder(@PathVariable String orderId){
        return orderService.getOrderById(orderId);
    }
}
