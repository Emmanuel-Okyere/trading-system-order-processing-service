package com.tlc.group.seven.orderprocessingservice.portfolio.repository;


import com.tlc.group.seven.orderprocessingservice.portfolio.model.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PortfolioRepository  extends JpaRepository<Portfolio,Long> {

    Optional<Portfolio> findPortfolioByTicker(String ticker);
    Optional<List<Portfolio>> findPortfoliosByUsers_iD(Long users_iD);

    Optional<Portfolio> findPortfolioByTickerAndUsers_iD(String ticker, Long users_iD);
}
