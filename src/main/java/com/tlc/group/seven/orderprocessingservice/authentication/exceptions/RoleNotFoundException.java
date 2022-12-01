package com.tlc.group.seven.orderprocessingservice.authentication.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class RoleNotFoundException extends ResponseStatusException {
    public RoleNotFoundException (HttpStatus status, String message){
        super(status,message);
    }
}
