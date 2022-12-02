package com.tlc.group.seven.orderprocessingservice.authentication.payload.response;

import lombok.Data;

import java.util.List;

@Data
public class JwtResponse {
    private String accessToken;
//    private String type = "Bearer";
    private Long id;
    private String name;
    private String email;

    private double balance;
    private String status;
    private List<String> roles;

    public JwtResponse(String accessToken, Long id, String name, String email, List<String> roles, double balance, String status) {
        this.accessToken = accessToken;
        this.id = id;
        this.name = name;
        this.email = email;
        this.balance = balance;
        this.roles = roles;
        this.status = status;
    }
}