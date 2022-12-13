package com.tlc.group.seven.orderprocessingservice.portfolio.service;

import com.tlc.group.seven.orderprocessingservice.portfolio.repository.PortfolioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
class PortfolioServiceTest {
    @Autowired
    PortfolioService portfolioService;
    @MockBean
    PortfolioRepository portfolioRepository;
    @Test
    void createPortfolio() {

    }

    @Test
    void getUserPortfolio() {

    }

    @Test
    void getAllOrdersByUser() {

    }

    @Test
    void deletePortfolio() {

    }

    @Test
    void toUpUserAccount() {

    }
}