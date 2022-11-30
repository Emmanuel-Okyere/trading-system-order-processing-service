package com.tlc.group.seven.orderprocessingservice.authentication.exceptions;

public class RoleNotFoundException extends Exception{
    public RoleNotFoundException (String message){
        super(message);
    }
}
