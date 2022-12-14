package com.tlc.group.seven.orderprocessingservice.order.repository;

import com.tlc.group.seven.orderprocessingservice.order.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order,Long> {

    Optional<Order> findOrderByOrderId(String  orderId);

    List<Order> findOrderByPortfolio_iD(Long id);
}
