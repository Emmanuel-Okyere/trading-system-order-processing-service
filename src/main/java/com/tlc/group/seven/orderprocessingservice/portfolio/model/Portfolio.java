package com.tlc.group.seven.orderprocessingservice.portfolio.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tlc.group.seven.orderprocessingservice.authentication.model.User;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"ticker","users_id"})})
public class Portfolio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long iD;
    @NotNull
    private String ticker;
    private int quantity;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id")
    private User users;

    public Portfolio(String defaultPortfolio) {
        this.ticker = defaultPortfolio;
    }
    public Portfolio(){

    }
}
