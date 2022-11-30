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
import javax.validation.constraints.Size;
import java.util.Date;
@Data
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long iD;
    private String product;

    private Integer quantity;
    private Double price;

    @NotBlank(message = "Type is required")
    private String type;
    @NotBlank(message = "Side needed")
    private String side;
    @Column
    private String orderId;
    @CreationTimestamp
    private Date createdAt;
    @UpdateTimestamp
    private Date updatedAt;

    @Override
    public String toString() {
        return "Order{" +
                "iD=" + iD +
                ", product='" + product + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                ", type='" + type + '\'' +
                ", side='" + side + '\'' +
                ", orderId='" + orderId + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", users=" + users +
                '}';
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id")
    private User users;

    public void setUser(User user){
        this.users= user;
    }
}
