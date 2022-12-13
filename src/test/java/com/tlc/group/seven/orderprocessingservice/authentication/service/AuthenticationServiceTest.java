package com.tlc.group.seven.orderprocessingservice.authentication.service;

import com.tlc.group.seven.orderprocessingservice.authentication.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationServiceTest {
    @MockBean
    UserRepository userRepository;
    @Autowired
    AuthenticationService authenticationService;

    @Test
    void authenticateUserLogin() {

    }

    @Test
    void registerUser() {

    }
}