package com.tlc.group.seven.orderprocessingservice.portfolio.service;

import com.tlc.group.seven.orderprocessingservice.authentication.model.User;
import com.tlc.group.seven.orderprocessingservice.authentication.repository.UserRepository;
import com.tlc.group.seven.orderprocessingservice.authentication.service.UserDetailsImpl;
import com.tlc.group.seven.orderprocessingservice.constant.ServiceConstants;
import com.tlc.group.seven.orderprocessingservice.order.model.Order;
import com.tlc.group.seven.orderprocessingservice.order.repository.OrderRepository;
import com.tlc.group.seven.orderprocessingservice.portfolio.model.Portfolio;
import com.tlc.group.seven.orderprocessingservice.portfolio.payload.TopUpRequest;
import com.tlc.group.seven.orderprocessingservice.portfolio.repository.PortfolioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PortfolioService {


    @Autowired
    private PortfolioRepository portfolioRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderRepository orderRepository;

    public ResponseEntity<?> createPortfolio(Portfolio portfolio){
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.getReferenceById(userDetails.getId());
        if(portfolioRepository.findPortfolioByTickerAndUsers_iD(portfolio.getTicker(),user.getID()).isEmpty()){
            portfolio.setUsers(user);
            portfolio.setQuantity(0);
            portfolioRepository.save(portfolio);
            Map<?,?> statusResponse = Map.of("status", ServiceConstants.successStatus,"message",ServiceConstants.portfolioCreationSuccess,"data", portfolio);
            return ResponseEntity.status(HttpStatus.CREATED).body(statusResponse);
        }
       else {
            Map<?,?> statusResponse = Map.of("status", ServiceConstants.failureStatus,"message",ServiceConstants.portfolioCreationFailure,"error", ServiceConstants.portfolioNameTaken);
           return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(statusResponse);
        }
    }

    public ResponseEntity<?> getUserPortfolio() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.getReferenceById(userDetails.getId());
        Optional<List<Portfolio>> portfolios = portfolioRepository.findPortfoliosByUsers_iD(user.getID());
        if(portfolios.isPresent()){
            Map<?,?> statusResponse = Map.of("status", ServiceConstants.successStatus,"message",ServiceConstants.portfolioGettingSuccess,"data", portfolios.get());
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(statusResponse);
        }
        else {
            Map<?,?> statusResponse = Map.of("status", ServiceConstants.successStatus,"message",ServiceConstants.portfolioGettingSuccess,"data", new ArrayList<>());
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(statusResponse);
        }
    }

    public ResponseEntity<?> getAllOrdersByUser(Long portfolioId) {
        List<Order> allByUsers_iD = orderRepository.findOrderByPortfolio_iD(portfolioId);
        Map<?,?> response = Map.of("status",ServiceConstants.successStatus,"message",ServiceConstants.ordersGettingSuccess, "data",allByUsers_iD);
        return  ResponseEntity.status(HttpStatus.FOUND)
                .body(response);
    }

    public ResponseEntity<?> deletePortfolio(Long portfolioId) {
        Optional<Portfolio> portfolio = portfolioRepository.findById(portfolioId);
        if(portfolio.isPresent()){
            if(portfolio.get().getTicker().equals(ServiceConstants.defaultPortfolio)){
                Map<?,?> response = Map.of("status",ServiceConstants.failureStatus,"message",ServiceConstants.defaultPortfolioDeleteFailure);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            else{
                portfolioRepository.delete(portfolio.get());
                Map<?,?> response = Map.of("status",ServiceConstants.successStatus,"message",ServiceConstants.portfolioDeleteSuccess);
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
            }
        }
        else {
            Map<?,?> response = Map.of("status",ServiceConstants.failureStatus,"message",ServiceConstants.portfolioDeleteFailure);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
    public ResponseEntity<?> toUpUserAccount(TopUpRequest topUpRequest) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.getReferenceById(userDetails.getId());
        user.setBalance(user.getBalance()+topUpRequest.getAmountToTopUp());
        userRepository.save(user);
        Map<?, ?> response = Map.of("status",ServiceConstants.successStatus, "message",ServiceConstants.topUpSuccess,"data",  Map.of("currentBalance",user.getBalance()));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }
}
