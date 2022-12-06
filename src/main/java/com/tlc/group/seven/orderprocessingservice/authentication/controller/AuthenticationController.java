package com.tlc.group.seven.orderprocessingservice.authentication.controller;


import com.tlc.group.seven.orderprocessingservice.authentication.exceptions.RoleNotFoundException;
import com.tlc.group.seven.orderprocessingservice.authentication.payload.request.LoginRequest;
import com.tlc.group.seven.orderprocessingservice.authentication.payload.request.SignupRequest;
import com.tlc.group.seven.orderprocessingservice.authentication.service.AuthenticationService;
import com.tlc.group.seven.orderprocessingservice.kafka.producer.KafkaProducer;
import com.tlc.group.seven.orderprocessingservice.log.system.SystemLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;

@CrossOrigin(origins = "*",maxAge = 3600)
@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {
    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    KafkaProducer kafkaProducer;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest){
        SystemLog systemLog = new SystemLog("authenticateUser", "user login", "user login initiated", "order-processing", new Date());
        kafkaProducer.sendResponseToKafkaLogData(systemLog);
        return authenticationService.authenticateUserLogin(loginRequest);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) throws RoleNotFoundException {
        SystemLog systemLog = new SystemLog("registerUser", "register user", "register new user initiated", "order-processing", new Date());
        kafkaProducer.sendResponseToKafkaLogData(systemLog);
        return authenticationService.registerUser(signUpRequest);
    }
}
