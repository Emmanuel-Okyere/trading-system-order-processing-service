package com.tlc.group.seven.orderprocessingservice.portfolio.controller;

import com.tlc.group.seven.orderprocessingservice.portfolio.model.Portfolio;
import com.tlc.group.seven.orderprocessingservice.portfolio.payload.TopUpRequest;
import com.tlc.group.seven.orderprocessingservice.portfolio.service.PortfolioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1/portfolio")
public class PortfolioController {
    @Autowired
    private PortfolioService portfolioService;

    @PostMapping
    public ResponseEntity<?> createPortfolio( @RequestBody @Valid Portfolio portfolio){
        return portfolioService.createPortfolio(portfolio);
    }
    @GetMapping
    public ResponseEntity<?> getPortfolioByUser(){
        return portfolioService.getUserPortfolio();
    }

    @GetMapping("/{portfolioId}/orders")
    public ResponseEntity<?> getAllOrderByAUser(@PathVariable Long portfolioId){
        return portfolioService.getAllOrdersByUser(portfolioId);
    }

    @DeleteMapping("/{portfolioId}")
    public ResponseEntity<?> deletePortfolioById(@PathVariable Long portfolioId){
        return portfolioService.deletePortfolio(portfolioId);
    }

    @PostMapping("/topup")
    public ResponseEntity<?> topUpAccount(@Valid @RequestBody TopUpRequest topUpRequest){
        return portfolioService.toUpUserAccount(topUpRequest);
    }
}
