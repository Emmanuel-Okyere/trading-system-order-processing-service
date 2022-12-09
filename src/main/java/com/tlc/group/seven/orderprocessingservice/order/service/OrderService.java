package com.tlc.group.seven.orderprocessingservice.order.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tlc.group.seven.orderprocessingservice.authentication.model.User;
import com.tlc.group.seven.orderprocessingservice.authentication.repository.UserRepository;
import com.tlc.group.seven.orderprocessingservice.authentication.service.UserDetailsImpl;
import com.tlc.group.seven.orderprocessingservice.constant.ServiceConstants;
import com.tlc.group.seven.orderprocessingservice.kafka.consumer.KafkaConsumer;
import com.tlc.group.seven.orderprocessingservice.log.system.service.SystemLogService;
import com.tlc.group.seven.orderprocessingservice.order.model.MarketData;
import com.tlc.group.seven.orderprocessingservice.order.model.Order;
import com.tlc.group.seven.orderprocessingservice.order.model.OrderExecution;
import com.tlc.group.seven.orderprocessingservice.order.payload.ErrorResponse;
import com.tlc.group.seven.orderprocessingservice.order.payload.OrderResponse;
import com.tlc.group.seven.orderprocessingservice.order.repository.OrderRepository;
import com.tlc.group.seven.orderprocessingservice.portfolio.model.Portfolio;
import com.tlc.group.seven.orderprocessingservice.portfolio.repository.PortfolioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private PortfolioRepository portfolioRepository;
    @Autowired
    private KafkaConsumer kafkaConsumer;
    @Autowired
    private SystemLogService systemLogService;
    private final String exchangeURL = ServiceConstants.exchangeURL;
    private final String exchange2URL = ServiceConstants.exchange2URL;
    WebClient webClient = WebClient.create(exchangeURL);

    public ResponseEntity<?> createOrder(Order order) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.getReferenceById(userDetails.getId());
        systemLogService.sendSystemLogToReportingService("createOrder", ServiceConstants.userTriggeredEvent, "User order creation initiated");
        switch (order.getSide().toLowerCase()) {
            case "sell":
                if (validateSellOrderAgainstUserPortfolio(order) && validateSellAgainstMarketData(order)) {
                    systemLogService.sendSystemLogToReportingService("createOrder", ServiceConstants.systemTriggeredEvent,"User order is valid so, making order to exhange");
                    return makeOrderToExchange(order, user);
                } else{
                    systemLogService.sendSystemLogToReportingService("createOrder", ServiceConstants.systemTriggeredEvent,"User sell order is invalid, OrderCanceled");
                    return ResponseEntity.status(HttpStatus.OK)
                            .body(ErrorResponse.builder()
                                    .status(ServiceConstants.failureStatus)
                                    .message(ServiceConstants.portfolioCannotMakeOrder).build());
                }
            case "buy": {
                if (validateBuyOrderAgainstUserPortfolio(order) && validateBuyAgainstMarketData(order)) {
                    systemLogService.sendSystemLogToReportingService("createOrder", ServiceConstants.systemTriggeredEvent,"User buy order is valid so, making order to exhange");
                    return makeOrderToExchange(order, user);
                } else {
                    systemLogService.sendSystemLogToReportingService("createOrder", ServiceConstants.systemTriggeredEvent,"User buy order is invalid, orderCanceled");
                    return ResponseEntity.status(HttpStatus.OK)
                        .body(ErrorResponse.builder()
                                .status(ServiceConstants.failureStatus)
                                .message(ServiceConstants.insufficientBalance).build());
            }
            }
        }
        systemLogService.sendSystemLogToReportingService("createOrder", ServiceConstants.systemTriggeredEvent,"User buy order is invalid, orderCanceled");
        return ResponseEntity.status(HttpStatus.OK)
                .body(ErrorResponse.builder()
                        .status(ServiceConstants.failureStatus)
                        .message(ServiceConstants.UnsuccessfullOrderCreation).build());
    }

    public ResponseEntity<?> getOrderById(String orderId) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.getReferenceById(userDetails.getId());
        systemLogService.sendSystemLogToReportingService("getOrderById", ServiceConstants.userTriggeredEvent, "User order getting by orderId initiated");
        Optional<Order> order = orderRepository.findOrderByOrderId(orderId);
        if (order.isPresent()) {
            try {
                OrderExecution response = webClient
                        .get()
                        .uri("/order/" + orderId)
                        .retrieve()
                        .bodyToMono(OrderExecution.class)
                        .block();
                if (response != null) {
                    response.setCreatedAt(order.get().getCreatedAt());
                    response.setUpdatedAt(new Date());
                    response.setOrderStatus(ServiceConstants.orderStatusOpen);
                    if (order.get().getOrderStatus().equals(ServiceConstants.orderStatusOpen)) {
                        if (response.getCumulatitiveQuantity() == order.get().getQuantity()) {
                            if(order.get().getSide().equalsIgnoreCase("sell")){
                                order.get().getPortfolio().setQuantity(order.get().getPortfolio().getQuantity()-order.get().getQuantity());
                            }
                            else{
                                order.get().getPortfolio().setQuantity(order.get().getPortfolio().getQuantity()+order.get().getQuantity());
                            }
                            portfolioRepository.save(order.get().getPortfolio());
                            order.get().setOrderStatus(ServiceConstants.orderStatusClose);
                            response.setOrderStatus(ServiceConstants.orderStatusClose);
                            user.setBalance(user.getBalance()+(response.getQuantity() * response.getCumulatitivePrice()));
                            systemLogService.sendSystemLogToReportingService("getOrderById", ServiceConstants.systemTriggeredEvent, "Updating users balance");
                            orderRepository.save(order.get());
                            userRepository.save(user);
                        }
                    }return ResponseEntity
                            .status(HttpStatus.OK)
                            .body(Map
                                    .of("status",ServiceConstants.successStatus,"message",ServiceConstants.ordersGettingSuccess,"data",response));
                }
            } catch (WebClientResponseException | WebClientRequestException e) {
                systemLogService.sendSystemLogToReportingService("getOrderById", ServiceConstants.userTriggeredEvent, "Oder does not exist for user");
                return ResponseEntity
                        .status(HttpStatus.OK)
                        .body(ErrorResponse
                                .builder()
                                .message(ServiceConstants.orderGettingError)
                                .status(ServiceConstants.failureStatus)
                                .build());
            }
        }
        systemLogService.sendSystemLogToReportingService("getOrderById", ServiceConstants.userTriggeredEvent, "Oder does not exist for user");
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ErrorResponse
                        .builder()
                        .message(ServiceConstants.orderGettingError)
                        .status(ServiceConstants.failureStatus)
                        .build());
    }

    public Boolean validateSellOrderAgainstUserPortfolio(Order order) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.getReferenceById(userDetails.getId());
        Optional<Portfolio> usersPortfolio = portfolioRepository.findById(order.getPortfolioId());
        if (usersPortfolio.isPresent()) {
            List<Order> userOrders = orderRepository.findOrderByPortfolio_iD(order.getPortfolioId());
            for (Order userOrder : userOrders) {
                if (userOrder.getProduct().equals(order.getProduct()) && userOrder.getOrderStatus().equals(ServiceConstants.orderStatusClose)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    public Boolean validateBuyOrderAgainstUserPortfolio(Order order) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.getReferenceById(userDetails.getId());
        Optional<Portfolio> usersPortfolio = portfolioRepository.findById(order.getPortfolioId());
        if (usersPortfolio.isPresent()) {
            return order.getPrice() * order.getQuantity() <= user.getBalance();
        }
        return false;
    }

    public ResponseEntity<?> makeOrderToExchange(Order order, User user) {
        systemLogService.sendSystemLogToReportingService("createOrder", ServiceConstants.systemTriggeredEvent, "Placing order on exchange initiated");
        try {
            String response = webClient
                    .post()
                    .uri("/order")
                    .body(Mono.just(order), Order.class)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            if (response != null) {
                order.setOrderId(response.substring(1, response.length() - 1));
                Optional<Portfolio> portfolio = portfolioRepository.findById(order.getPortfolioId());
                if (portfolio.isPresent()) {
                    order.setPortfolio(portfolio.get());
                    order.setOrderStatus(ServiceConstants.orderStatusOpen);
                    orderRepository.save(order);
//                    if(order.getSide().equalsIgnoreCase("buy")){
                    double totalOrderPrice = order.getPrice() * order.getQuantity();
                    user.setBalance(user.getBalance() - totalOrderPrice);
                    userRepository.save(user);
//                    }
                    OrderResponse orderResponse = new OrderResponse(
                            order.getID(), order.getOrderId(),
                            order.getQuantity(), order.getProduct(),
                            order.getPrice(), order.getType(),
                            order.getOrderStatus());
                    systemLogService.sendSystemLogToReportingService("makeOrderToExchange", ServiceConstants.systemTriggeredEvent, "Oder created on exchange");
                    Map<?, ?> statusResponse = Map.of("status", ServiceConstants.successStatus, "message", ServiceConstants.orderCreationSuccess, "data", orderResponse);
                    return ResponseEntity.status(HttpStatus.CREATED).body(statusResponse);
                }

            }
        } catch (WebClientResponseException | WebClientRequestException e) {
            systemLogService.sendSystemLogToReportingService("makeOrderToExchange", ServiceConstants.systemTriggeredEvent, "Order creation unsuccessful");
            return ResponseEntity.status(HttpStatus.OK)
                    .body(ErrorResponse.builder()
                            .status(ServiceConstants.failureStatus)
                            .message(ServiceConstants.UnsuccessfullOrderCreation).build());
        }
        systemLogService.sendSystemLogToReportingService("makeOrderToExchange", ServiceConstants.systemTriggeredEvent, "Order creation unsuccessful");
        return ResponseEntity.status(HttpStatus.OK)
                .body(ErrorResponse.builder()
                        .status(ServiceConstants.failureStatus)
                        .message(ServiceConstants.UnsuccessfullOrderCreation).build());
    }

    public Boolean validateBuyAgainstMarketData(Order order) {
        ObjectMapper mapper = new ObjectMapper();
        List<MarketData> marketData = mapper.convertValue(kafkaConsumer.payload, new TypeReference<>() {
        });
        List<?> marketData1 = marketData.stream().filter(data -> data.getTICKER().equals(order.getProduct()))
                .filter(x -> ((Math.abs(x.getASK_PRICE() - order.getPrice()) >= 0 && Math.abs(x.getASK_PRICE() - order.getPrice()) <= 5.0)) && (order.getQuantity()) <= x.getSELL_LIMIT())
                .toList();
        return !marketData1.isEmpty();
    }

    public Boolean validateSellAgainstMarketData(Order order) {
        ObjectMapper mapper = new ObjectMapper();
        List<MarketData> marketData = mapper.convertValue(kafkaConsumer.payload, new TypeReference<>() {
        });
        List<?> marketData1 = marketData.stream()
                .filter(x -> x.getTICKER().equals(order.getProduct()))
                .filter(x -> ((Math.abs(x.getBID_PRICE() - order.getPrice()) >= 0 && (Math.abs(x.getBID_PRICE() - order.getPrice())) <=5.0)) && (order.getQuantity()) <= x.getBUY_LIMIT())
                .toList();
        return !marketData1.isEmpty();
    }

    public ResponseEntity<?> cancelOrder(String orderId) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.getReferenceById(userDetails.getId());
        systemLogService.sendSystemLogToReportingService("cancelOrder", ServiceConstants.userTriggeredEvent, "Order Cancellation initiated");
        ResponseEntity<?> response = getOrderById(orderId);
        OrderExecution orderExecution = (OrderExecution) response.getBody();
        Optional<Order> order = orderRepository.findOrderByOrderId(orderId);
        if (order.isPresent()) {
            if (order.get().getOrderStatus().equals(ServiceConstants.orderStatusOpen)) {
                if (orderExecution != null) {
                    double executedAmount = orderExecution.getPrice() * (orderExecution.getQuantity()-orderExecution.getCumulatitiveQuantity());
                    if(cancelOrderOnExchange(orderId)){
                        user.setBalance(user.getBalance() + executedAmount);
                        orderRepository.delete(order.get());
                        userRepository.save(user);
                        systemLogService.sendSystemLogToReportingService("cancelOrder", ServiceConstants.systemTriggeredEvent, "Order Cancellation successful");
                        return ResponseEntity
                                .status(HttpStatus.NO_CONTENT)
                                .body(Map
                                        .of("status",ServiceConstants.successStatus, "message",ServiceConstants.orderCancelSuccess));
                    }
                }
            }
        }
        systemLogService.sendSystemLogToReportingService("cancelOrder", ServiceConstants.systemTriggeredEvent, "Order Cancellation unsuccessful");
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(Map
                        .of("status",ServiceConstants.failureStatus, "message",ServiceConstants.orderCancelFailure));
    }


    public Boolean cancelOrderOnExchange(String orderId) {
        try {
            Boolean response = webClient
                    .delete()
                    .uri("/order/" + orderId)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block();
            if(response == Boolean.TRUE){
                return true;
            }
        } catch (WebClientResponseException | WebClientRequestException e) {
            return false;
        }
        return false;
    }
}
