package com.tlc.group.seven.orderprocessingservice.authentication.payload.response;

import lombok.Data;

@Data
public class UserCreationResponse {

    private Long id;
    private String name;
    private String email;

    private double balance;
    public UserCreationResponse(Long id, String name, String email, Double balance) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.balance = balance;
    }
}
