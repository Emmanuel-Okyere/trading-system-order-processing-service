package com.tlc.group.seven.orderprocessingservice.config;

import com.tlc.group.seven.orderprocessingservice.constant.ServiceConstants;
import com.tlc.group.seven.orderprocessingservice.order.payload.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class ControllerAdviceConfig {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, ?>> handleEntityValidationExceptions(MethodArgumentNotValidException methodArgumentNotValidException) {

        Map<String, String> body = methodArgumentNotValidException
                .getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));

        Map<String, ?> errors = Map.
                of("errors", body,"status",ServiceConstants.failureStatus, "message",ServiceConstants.creationFailure);
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity <ErrorResponse> handleAllResponseEntity(ResponseStatusException responseStatusException){
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(ServiceConstants.failureStatus)
                .message(ServiceConstants.roleNotFoundFailure)
                .build();
        return ResponseEntity.badRequest().body(errorResponse);

    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity <ErrorResponse> handleUserNotFoundException(UsernameNotFoundException usernameNotFoundException){
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(ServiceConstants.failureStatus)
                .message(ServiceConstants.userNotFoundFailure)
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
}
