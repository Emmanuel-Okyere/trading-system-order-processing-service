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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import static com.tlc.group.seven.orderprocessingservice.constant.ServiceConstants.exchangeURL;

@Service
public class OrderService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderRepository repository;

//    @Autowired
//    public OrderService(OrderRepository repository) {
//        this.repository = repository;
//    }
    private final String exchangeURL = ServiceConstants.exchangeURL;
    private final String exchange2URL = ServiceConstants.exchange2URL;
    WebClient webClient = WebClient.create(exchangeURL);

    public OrderResponse createOrder(Order order){
        try{
            String response = webClient
                    .post()
                    .uri("/order")
                    .body(Mono.just(order), Order.class)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            if (response !=null){
                order.setOrderId(response.substring(1,response.length()-1));
                UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                User user  = userRepository.getReferenceById(userDetails.getId());
                order.setUsers(user);
                System.out.println(order);
//                repository.save(order);
                System.out.println(user.getName());
                return new OrderResponse(ServiceConstants.successStatus,ServiceConstants.orderCreationSuccess,
                        order.getID(),order.getOrderId(),
                        order.getQuantity(),order.getProduct(),
                        order.getPrice(),order.getType());
            }
        }catch (WebClientResponseException e){
            return null;
        }

        return null;
    }

    public ResponseEntity getOrderById(String orderId) {
        try {
            OrderExecution response = webClient
                    .get()
                    .uri("/order/" + orderId)
                    .retrieve()
                    .bodyToMono(OrderExecution.class)
                    .block();
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(response);
        }catch (WebClientResponseException e){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ErrorResponse
                            .builder()
                    .message(e.getMessage())
                    .status(e.getStatusCode()
                            .name())
                            .build());
        }
    }
}
