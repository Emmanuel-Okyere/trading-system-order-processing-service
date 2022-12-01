package com.tlc.group.seven.orderprocessingservice.order.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tlc.group.seven.orderprocessingservice.authentication.model.User;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.Min;
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
//    @Size(max = 50)
    private String product;
//    @NotBlank(message = "Quantity can not be empty")
    @NotNull
    private Integer quantity;
//    @NotBlank(message = "Price can not be empty")
    @NotNull
    private Double price;

    @NotBlank(message = "Type is required")
    @Size(min = 2)
    private String type;
    @NotBlank(message = "Side needed")
    @Size (min = 3)
    private String side;
    @Column
    private String orderId;
    @CreationTimestamp
    private Date createdAt;
    @UpdateTimestamp
    private Date updatedAt;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id")
    private User users;

    public void setUser(User user){
        this.users= user;
    }
}
