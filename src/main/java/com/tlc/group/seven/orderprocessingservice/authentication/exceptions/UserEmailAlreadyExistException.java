package com.tlc.group.seven.orderprocessingservice.authentication.exceptions;

public class UserEmailAlreadyExistException extends Exception{
    public UserEmailAlreadyExistException(String message) {
        super(message);
    }
}
