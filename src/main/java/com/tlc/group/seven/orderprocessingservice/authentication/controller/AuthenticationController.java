package com.tlc.group.seven.orderprocessingservice.authentication.controller;


import com.tlc.group.seven.orderprocessingservice.authentication.exceptions.RoleNotFoundException;
import com.tlc.group.seven.orderprocessingservice.authentication.payload.request.LoginRequest;
import com.tlc.group.seven.orderprocessingservice.authentication.payload.request.SignupRequest;
import com.tlc.group.seven.orderprocessingservice.authentication.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin(origins = "*",maxAge = 3600)
@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {
    @Autowired
    AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest){
        return authenticationService.authenticateUserLogin(loginRequest);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) throws RoleNotFoundException {
        return authenticationService.registerUser(signUpRequest);
    }
}
