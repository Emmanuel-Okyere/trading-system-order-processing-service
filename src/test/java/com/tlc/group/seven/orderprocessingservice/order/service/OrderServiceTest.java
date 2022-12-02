package com.tlc.group.seven.orderprocessingservice.order.service;

import com.tlc.group.seven.orderprocessingservice.order.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
class OrderServiceTest {
    @MockBean
    OrderRepository orderRepository;

    @Autowired
    OrderService orderService;

    @Test
    void createOrder() {

    }

    @Test
    void getOrderById() {
    }

    @Test
    void getAllOrdersByUser() {
    }

    @Test
    void validateOrderByUser() {
    }
}