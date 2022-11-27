package com.tlc.group.seven.orderprocessingservice.authentication.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long iD;
    private String name;
    private String email;
    private String password;
    private Boolean isAdmin;
    private Double balance;
    private Date createdAt;
    private Date updatedAt;
}
