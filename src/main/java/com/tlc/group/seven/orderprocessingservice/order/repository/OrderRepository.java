package com.tlc.group.seven.orderprocessingservice.order.repository;

import com.tlc.group.seven.orderprocessingservice.order.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order,Long> {

}
