package com.tlc.group.seven.orderprocessingservice.order.service;

import com.tlc.group.seven.orderprocessingservice.order.model.Order;
import com.tlc.group.seven.orderprocessingservice.order.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;

@Service
public class OrderService {

    private OrderRepository repository;

    @Autowired
    public OrderService(OrderRepository repository) {
        this.repository = repository;
    }

    public ResponseEntity<?> createOrder(Order order){
        String exchangeURL = "https://exchange.matraining.com/1567ae46-8d44-4210-bf91-9d5c61290d0f";
        String exchange2URL = "https://exchange2.matraining.com/1567ae46-8d44-4210-bf91-9d5c61290d0f";
        WebClient webClient = WebClient.create(exchangeURL);
        System.out.println(order.getQuantity());
        String response = webClient.post().uri("/order").body(Mono.just(order), Order.class).retrieve().bodyToMono(String.class).block();
        assert response != null;
        order.setOrderId(response.substring(1,response.length()-1));
        repository.save(order);
        HashMap<String,Object> orderCreatedResponse = new HashMap<>();
        orderCreatedResponse.put("status", "00");
        orderCreatedResponse.put("message", "order created successfully");
        orderCreatedResponse.put("id", order.getID());
        orderCreatedResponse.put("orderId", order.getOrderId());
        orderCreatedResponse.put("quantity", order.getQuantity());
        orderCreatedResponse.put("product", order.getProduct());
        orderCreatedResponse.put("price", order.getPrice());
        orderCreatedResponse.put("type", order.getType());
        return ResponseEntity.status(HttpStatus.CREATED).body(orderCreatedResponse);
    }
}
