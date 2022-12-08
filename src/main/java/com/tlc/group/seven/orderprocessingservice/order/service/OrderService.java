package com.tlc.group.seven.orderprocessingservice.order.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tlc.group.seven.orderprocessingservice.authentication.model.User;
import com.tlc.group.seven.orderprocessingservice.authentication.repository.UserRepository;
import com.tlc.group.seven.orderprocessingservice.authentication.service.UserDetailsImpl;
import com.tlc.group.seven.orderprocessingservice.constant.ServiceConstants;
import com.tlc.group.seven.orderprocessingservice.kafka.consumer.KafkaConsumer;
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
    private final String exchangeURL = ServiceConstants.exchangeURL;
    private final String exchange2URL = ServiceConstants.exchange2URL;
    WebClient webClient = WebClient.create(exchangeURL);

    public ResponseEntity<?> createOrder(Order order) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.getReferenceById(userDetails.getId());
        switch (order.getSide().toLowerCase()) {
            case "sell":
                if (validateSellOrderAgainstUserPortfolio(order) && validateSellAgainstMarketData(order)) {
                    return makeOrderToExchange(order, user);
                } else return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ErrorResponse.builder()
                                .status(ServiceConstants.failureStatus)
                                .message(ServiceConstants.portfolioCannotMakeOrder).build());
            case "buy": {
                if (validateBuyOrderAgainstUserPortfolio(order) && validateBuyAgainstMarketData(order)) {
                    return makeOrderToExchange(order, user);
                } else return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ErrorResponse.builder()
                                .status(ServiceConstants.failureStatus)
                                .message(ServiceConstants.insufficientBalance).build());
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.builder()
                        .status(ServiceConstants.failureStatus)
                        .message(ServiceConstants.UnsuccessfullOrderCreation).build());
    }

    public ResponseEntity<?> getOrderById(String orderId) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.getReferenceById(userDetails.getId());
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
                    if (order.get().getOrderStatus().equals(ServiceConstants.orderStatusOpen)) {
                        if (response.getCommulativeQuantity() == order.get().getQuantity()) {
                            order.get().getPortfolio().setQuantity(response.getCommulativeQuantity());
                            portfolioRepository.save(order.get().getPortfolio());
                            order.get().setOrderStatus(ServiceConstants.orderStatusClose);
                            user.setBalance(response.getCommulativeQuantity() * response.getPrice());
                            orderRepository.save(order.get());
                            userRepository.save(user);
                        }
                    }
                    return ResponseEntity
                            .status(HttpStatus.OK)
                            .body(response);
                }
            } catch (WebClientResponseException | WebClientRequestException e) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(ErrorResponse
                                .builder()
                                .message(ServiceConstants.orderGettingError)
                                .status(ServiceConstants.failureStatus)
                                .build());
            }
        }
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
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
                if (userOrder.getProduct().equals(order.getProduct())) { //user order.getStatus==closed so see that they own it.
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
                    double totalOrderPrice = order.getPrice() * order.getQuantity();
                    user.setBalance(user.getBalance() - totalOrderPrice);
                    userRepository.save(user);
                    OrderResponse orderResponse = new OrderResponse(
                            order.getID(), order.getOrderId(),
                            order.getQuantity(), order.getProduct(),
                            order.getPrice(), order.getType());
                    Map<?, ?> statusResponse = Map.of("status", ServiceConstants.successStatus, "message", ServiceConstants.orderCreationSuccess, "data", orderResponse);
                    return ResponseEntity.status(HttpStatus.CREATED).body(statusResponse);
                }

            }
        } catch (WebClientResponseException | WebClientRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ErrorResponse.builder()
                            .status(ServiceConstants.failureStatus)
                            .message(ServiceConstants.UnsuccessfullOrderCreation).build());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.builder()
                        .status(ServiceConstants.failureStatus)
                        .message(ServiceConstants.UnsuccessfullOrderCreation).build());
    }

    public Boolean validateBuyAgainstMarketData(Order order) {
        ObjectMapper mapper = new ObjectMapper();
        List<MarketData> marketData = mapper.convertValue(kafkaConsumer.payload, new TypeReference<>() {
        });
        List<?> marketData1 = marketData.stream().filter(data -> data.getTICKER().equals(order.getProduct()))
                .filter(x -> ((Math.abs(x.getASK_PRICE() - order.getPrice()) >= 0 && Math.abs(x.getASK_PRICE() - order.getPrice()) <= 1.0)) && (order.getQuantity()) <= x.getSELL_LIMIT())
                .toList();
        return !marketData1.isEmpty();
    }

    public Boolean validateSellAgainstMarketData(Order order) {
        ObjectMapper mapper = new ObjectMapper();
        List<MarketData> marketData = mapper.convertValue(kafkaConsumer.payload, new TypeReference<>() {
        });
        List<?> marketData1 = marketData.stream()
                .filter(x -> x.getTICKER().equals(order.getProduct()))
                .filter(x -> ((Math.abs(x.getBID_PRICE() - order.getPrice()) >= 0 && (Math.abs(x.getBID_PRICE() - order.getPrice())) <=1)) && (order.getQuantity()) <= x.getBUY_LIMIT())
                .toList();
        return !marketData1.isEmpty();
    }

    public ResponseEntity<?> cancelOrder(String orderId) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.getReferenceById(userDetails.getId());
        Optional<Order> order = orderRepository.findOrderByOrderId(orderId);
        if (order.isPresent()) {
            if (order.get().getOrderStatus().equals(ServiceConstants.orderStatusOpen)) {
                ResponseEntity<?> response = getOrderById(orderId);
                OrderExecution orderExecution = (OrderExecution) response.getBody();
                if (orderExecution != null) {
                    double executedAmount = orderExecution.getCumulatitivePrice() * orderExecution.getCommulativeQuantity();
                    if(cancelOrderOnExchange(orderId)){
                        user.setBalance(user.getBalance() + executedAmount);
                        orderRepository.delete(order.get());
                        userRepository.save(user);
                        return ResponseEntity
                                .status(HttpStatus.NO_CONTENT)
                                .body(Map
                                        .of("status",ServiceConstants.successStatus, "message",ServiceConstants.orderCancelSuccess));
                    }
                }
            }
        }
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
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
