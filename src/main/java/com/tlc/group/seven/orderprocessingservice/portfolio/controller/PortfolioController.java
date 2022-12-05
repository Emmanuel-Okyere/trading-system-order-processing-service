package com.tlc.group.seven.orderprocessingservice.portfolio.controller;

import com.tlc.group.seven.orderprocessingservice.portfolio.model.Portfolio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.websocket.server.PathParam;

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

    @DeleteMapping("/{portofolioId}")
    public ResponseEntity<?> deletePortfolioById(@PathVariable Long portofolioId){
        return portfolioService.deletePortfolio(portofolioId);
    }
}
