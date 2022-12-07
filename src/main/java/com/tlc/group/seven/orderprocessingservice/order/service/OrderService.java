package com.tlc.group.seven.orderprocessingservice.order.service;

import com.tlc.group.seven.orderprocessingservice.authentication.model.User;
import com.tlc.group.seven.orderprocessingservice.authentication.repository.UserRepository;
import com.tlc.group.seven.orderprocessingservice.authentication.service.UserDetailsImpl;
import com.tlc.group.seven.orderprocessingservice.constant.ServiceConstants;
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
    private final String exchangeURL = ServiceConstants.exchangeURL;
    private final String exchange2URL = ServiceConstants.exchange2URL;
    WebClient webClient = WebClient.create(exchangeURL);

    public ResponseEntity<?> createOrder(Order order) {
        switch (order.getSide().toLowerCase()){
            case "sell" :
                if(validateSellOrderAgainstUserPortfolio(order)){
                    return makeOrderToExchange(order);
                }
                                else return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ErrorResponse.builder()
                                .status(ServiceConstants.failureStatus)
                                .message(ServiceConstants.portfolioCannotMakeOrder).build());
            case "buy" :{
                if(validateBuyOrderAgainstUserPortfolio(order)){
                    return makeOrderToExchange(order);
                }
                else return ResponseEntity.status(HttpStatus.BAD_REQUEST)
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
        Optional<Order> order = orderRepository.findOrderByOrderId(orderId);
        if (order.isPresent()) {
            try {

                OrderExecution response = webClient
                        .get()
                        .uri("/order/" + orderId)
                        .retrieve()
                        .bodyToMono(OrderExecution.class)
                        .block();
                if (response !=null) {
                    response.setCreatedAt(order.get().getCreatedAt());
                    response.setUpdatedAt(new Date());
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

    public void validateSellOrderWithPortfolio(Order order){
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.getReferenceById(userDetails.getId());
    }

    public Boolean validateSellOrderAgainstUserPortfolio(Order order){
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.getReferenceById(userDetails.getId());
        Optional<Portfolio> usersPortfolio = portfolioRepository.findById(order.getPortfolioId());
        if(usersPortfolio.isPresent()){
            List<Order> userOrders = orderRepository.findOrderByPortfolio_iD(order.getPortfolioId());
            for(Order userOrder :userOrders){
                if(userOrder.getProduct().equals(order.getProduct()) ){ //user order.getStatus==closed so see that they own it.
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    public Boolean validateBuyOrderAgainstUserPortfolio(Order order){
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.getReferenceById(userDetails.getId());
        Optional<Portfolio> usersPortfolio = portfolioRepository.findById(order.getPortfolioId());
        if(usersPortfolio.isPresent()){
            if(order.getPrice()*order.getQuantity()<= usersPortfolio.get().getBalance()){
                System.out.println("************Can not make beacuse your money small****************");
                return true;
            }
            System.out.println("************Can not make bessssssacuse your money small****************");
            return false;
        }
        return false;
    }
    
    public ResponseEntity<?> makeOrderToExchange(Order order){
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
                if(portfolio.isPresent()){
                    order.setPortfolio(portfolio.get());
                    orderRepository.save(order);
                    OrderResponse orderResponse = new OrderResponse(
                            order.getID(), order.getOrderId(),
                            order.getQuantity(), order.getProduct(),
                            order.getPrice(), order.getType());
                    Map <?,?> statusResponse = Map.of("status", ServiceConstants.successStatus,"message",ServiceConstants.orderCreationSuccess,"data",orderResponse);
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
    
}