package com.tlc.group.seven.orderprocessingservice.authentication.payload.response;

import lombok.Data;

import java.util.List;

@Data
public class JwtResponse {
    private String accessToken;
    private String type = "Bearer";
    private Long id;
    private String name;
    private String email;
    private List<String> roles;

    public JwtResponse(String accessToken, Long id, String name, String email, List<String> roles) {
        this.accessToken = accessToken;
        this.id = id;
        this.name = name;
        this.email = email;
        this.roles = roles;
    }
}