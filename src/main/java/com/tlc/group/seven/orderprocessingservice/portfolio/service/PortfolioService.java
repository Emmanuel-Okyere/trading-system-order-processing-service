package com.tlc.group.seven.orderprocessingservice.portfolio.service;

import com.tlc.group.seven.orderprocessingservice.authentication.model.User;
import com.tlc.group.seven.orderprocessingservice.authentication.repository.UserRepository;
import com.tlc.group.seven.orderprocessingservice.authentication.service.UserDetailsImpl;
import com.tlc.group.seven.orderprocessingservice.constant.ServiceConstants;
import com.tlc.group.seven.orderprocessingservice.log.system.service.SystemLogService;
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
    @Autowired
    private SystemLogService systemLogService;

    public ResponseEntity<?> createPortfolio(Portfolio portfolio){
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.getReferenceById(userDetails.getId());
        systemLogService.sendSystemLogToReportingService("createPortfolio", ServiceConstants.userTriggeredEvent, "Portfolio Creation Initiated");
        if(portfolioRepository.findPortfolioByTickerAndUsers_iD(portfolio.getTicker(),user.getID()).isEmpty()){
            portfolio.setUsers(user);
            portfolio.setQuantity(0);
            portfolioRepository.save(portfolio);
            systemLogService.sendSystemLogToReportingService("createPortfolio", ServiceConstants.systemTriggeredEvent, "Portfolio Creation successful");
            Map<?,?> statusResponse = Map.of("status", ServiceConstants.successStatus,"message",ServiceConstants.portfolioCreationSuccess,"data", portfolio);
            return ResponseEntity.status(HttpStatus.CREATED).body(statusResponse);
        }
       else {
            Map<?,?> statusResponse = Map.of("status", ServiceConstants.failureStatus,"message",ServiceConstants.portfolioCreationFailure,"error", ServiceConstants.portfolioNameTaken);
           return  ResponseEntity.status(HttpStatus.OK).body(statusResponse);
        }
    }

    public ResponseEntity<?> getUserPortfolio() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.getReferenceById(userDetails.getId());
        Optional<List<Portfolio>> portfolios = portfolioRepository.findPortfoliosByUsers_iD(user.getID());
        if(portfolios.isPresent()){
            Map<?,?> statusResponse = Map.of("status", ServiceConstants.successStatus,"message",ServiceConstants.portfolioGettingSuccess,"data", portfolios.get());
            return  ResponseEntity.status(HttpStatus.OK).body(statusResponse);
        }
        else {
            Map<?,?> statusResponse = Map.of("status", ServiceConstants.successStatus,"message",ServiceConstants.portfolioGettingSuccess,"data", new ArrayList<>());
            return  ResponseEntity.status(HttpStatus.OK).body(statusResponse);
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
        systemLogService.sendSystemLogToReportingService("deletePortfolio", ServiceConstants.userTriggeredEvent, "Portfolio Deletion Initiated: "+portfolioId);
        if(portfolio.isPresent()){
            if(portfolio.get().getTicker().equals(ServiceConstants.defaultPortfolio)){
                Map<?,?> response = Map.of("status",ServiceConstants.failureStatus,"message",ServiceConstants.defaultPortfolioDeleteFailure);
                return ResponseEntity.status(HttpStatus.OK).body(response);
            }
            else{
                List<Order> openOrders = orderRepository.findOrderByPortfolio_iD(portfolioId).stream().filter(order -> order.getOrderStatus().equals(ServiceConstants.orderStatusOpen)).toList();
                if(openOrders.size() > 0){
                    systemLogService.sendSystemLogToReportingService("deletePortfolio", ServiceConstants.systemTriggeredEvent, "Error deleting: "+portfolioId + " because it has open orders");
                    Map<?,?> response = Map.of("status",ServiceConstants.failureStatus,"message",ServiceConstants.openOrderPortfolioDeleteFailure);
                    return ResponseEntity.status(HttpStatus.OK).body(response);
                }
                systemLogService.sendSystemLogToReportingService("deletePortfolio", ServiceConstants.systemTriggeredEvent, "Portfolio Deletion Successful: "+portfolioId);
                portfolioRepository.delete(portfolio.get());
                Map<?,?> response = Map.of("status",ServiceConstants.successStatus,"message",ServiceConstants.portfolioDeleteSuccess);
                return ResponseEntity.status(HttpStatus.OK).body(response);
            }
        }
        else {
            Map<?,?> response = Map.of("status",ServiceConstants.failureStatus,"message",ServiceConstants.portfolioDeleteFailure);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
    }
    public ResponseEntity<?> toUpUserAccount(TopUpRequest topUpRequest) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.getReferenceById(userDetails.getId());
        systemLogService.sendSystemLogToReportingService("toUpUserAccount", ServiceConstants.userTriggeredEvent, "Account Top initiated");
        user.setBalance(user.getBalance()+topUpRequest.getAmountToTopUp());
        systemLogService.sendSystemLogToReportingService("toUpUserAccount", ServiceConstants.userTriggeredEvent, "Account Top successful");
        userRepository.save(user);
        Map<?, ?> response = Map.of("status",ServiceConstants.successStatus, "message",ServiceConstants.topUpSuccess,"data",  Map.of("currentBalance",user.getBalance()));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }
}
