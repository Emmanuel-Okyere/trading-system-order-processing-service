package com.tlc.group.seven.orderprocessingservice.authentication.payload.response;

import lombok.Data;

@Data
public class UserCreationResponse {

    private Long id;
    private String name;
    private String email;

    private double balance;
    private String status;
    private String message;

    public UserCreationResponse(Long id, String name, String email, String status, String message) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.status = status;
        this.message = message;
    }
}
