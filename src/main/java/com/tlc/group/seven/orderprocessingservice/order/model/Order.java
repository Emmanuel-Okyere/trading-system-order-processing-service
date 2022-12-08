package com.tlc.group.seven.orderprocessingservice.order.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tlc.group.seven.orderprocessingservice.portfolio.model.Portfolio;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
@Data
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long iD;
    @NotBlank(message = "Product can not be empty")
    private String product;
    @NotNull
    private Integer quantity;
    @NotNull
    private Double price;
    @JsonIgnoreProperties
    private String orderStatus;
    @NotBlank(message = "Type is required")
    @Size(min = 2)
    private String type;
    @NotBlank(message = "Side must not be null")
    @Size (min = 3)
    private String side;
    @Column
    private String orderId;
    @CreationTimestamp
    private Date createdAt;
    @UpdateTimestamp
    private Date updatedAt;

    @NotNull
    @Transient
    private Long portfolioId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id")
    private Portfolio portfolio;

    public void setPortfolio(Portfolio portfolio){
        this.portfolio= portfolio;
    }
}
