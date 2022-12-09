package com.tlc.group.seven.orderprocessingservice.authentication.service;

import com.tlc.group.seven.orderprocessingservice.authentication.exceptions.RoleNotFoundException;
import com.tlc.group.seven.orderprocessingservice.authentication.model.ERole;
import com.tlc.group.seven.orderprocessingservice.authentication.model.Role;
import com.tlc.group.seven.orderprocessingservice.authentication.model.User;
import com.tlc.group.seven.orderprocessingservice.authentication.payload.request.LoginRequest;
import com.tlc.group.seven.orderprocessingservice.authentication.payload.request.SignupRequest;
import com.tlc.group.seven.orderprocessingservice.authentication.payload.response.JwtResponse;
import com.tlc.group.seven.orderprocessingservice.authentication.payload.response.UserCreationResponse;
import com.tlc.group.seven.orderprocessingservice.authentication.repository.RoleRepository;
import com.tlc.group.seven.orderprocessingservice.authentication.repository.UserRepository;
import com.tlc.group.seven.orderprocessingservice.authentication.security.jwt.JwtUtils;
import com.tlc.group.seven.orderprocessingservice.constant.ServiceConstants;
import com.tlc.group.seven.orderprocessingservice.log.system.service.SystemLogService;
import com.tlc.group.seven.orderprocessingservice.portfolio.model.Portfolio;
import com.tlc.group.seven.orderprocessingservice.portfolio.repository.PortfolioRepository;
import com.tlc.group.seven.orderprocessingservice.portfolio.service.PortfolioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class AuthenticationService {
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    JwtUtils jwtUtils;
    @Autowired
    PortfolioService portfolioService;
    @Autowired
    PortfolioRepository portfolioRepository;

    @Autowired
    SystemLogService systemLogService;

    public ResponseEntity<?> authenticateUserLogin(LoginRequest loginRequest){
        systemLogService.sendSystemLogToReportingService("authenticateUserLogin", ServiceConstants.userTriggeredEvent, "user initiated login");
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userRepository.getReferenceById(userDetails.getId());
        Optional<Portfolio> portfolio = portfolioRepository.findPortfolioByTickerAndUsers_iD(ServiceConstants.defaultPortfolio, user.getID());
        if(portfolio.isEmpty()){
            Portfolio defaultPortfolio = new Portfolio(ServiceConstants.defaultPortfolio);
            defaultPortfolio.setUsers(user);
            defaultPortfolio.setQuantity(0);
            portfolioRepository.save(defaultPortfolio);
            systemLogService.sendSystemLogToReportingService("portfolio.isEmpty()", ServiceConstants.systemTriggeredEvent, "default portfolio created on init login if portfolio empty");
        }
        List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
        Map<?, ?> response = Map
                .of("status",ServiceConstants.successStatus,
                        "message",ServiceConstants.userLoginSuccess,
                        "accessToken",jwt,
                        "data", new JwtResponse(userDetails.getId(),userDetails.getName(),userDetails.getEmail(),
                                roles,userDetails.getBalance(), ServiceConstants.successStatus));
        systemLogService.sendSystemLogToReportingService("authenticateUserLogin", ServiceConstants.systemTriggeredEvent, ServiceConstants.userLoginSuccess + " user_id: " + userDetails.getId());
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> registerUser(SignupRequest signUpRequest) throws RoleNotFoundException {
        systemLogService.sendSystemLogToReportingService("registerUser", ServiceConstants.userTriggeredEvent, "new user is creating account");

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            Map<String,String> hashResponse = Map
                    .of("status", ServiceConstants.failureStatus,"message", ServiceConstants.emailAlreadyTaken);
            systemLogService.sendSystemLogToReportingService("userRepository.existsByEmail", ServiceConstants.userTriggeredEvent, ServiceConstants.emailAlreadyTaken);
            return ResponseEntity
                    .badRequest()
                    .body(hashResponse);
        }
        // Create new user's account
        User user = new User(signUpRequest.getName(),
                signUpRequest.getEmail(),
                passwordEncoder.encode(signUpRequest.getPassword()));

        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();
        if (strRoles == null) {
            systemLogService.sendSystemLogToReportingService("create account", ServiceConstants.systemTriggeredEvent, "strRoles is null :: user");

            Role userRole = roleRepository.findByName(ERole.USER)
                    .orElseThrow(() -> new RoleNotFoundException(HttpStatus.BAD_REQUEST,ServiceConstants.roleNotFoundFailure));
            roles.add(userRole);
        } else {
            for (String role : strRoles) {
                switch (role) {
                    case "admin" -> {
                        Role adminRole = roleRepository.findByName(ERole.ADMIN)
                                .orElseThrow(() -> new RoleNotFoundException(HttpStatus.BAD_REQUEST,ServiceConstants.roleNotFoundFailure));
                        roles.add(adminRole);
                    }
                    default -> {
                        Role userRole = roleRepository.findByName(ERole.USER)
                                .orElseThrow(() -> new RoleNotFoundException(HttpStatus.BAD_REQUEST,ServiceConstants.roleNotFoundFailure));
                        roles.add(userRole);
                    }
                }
            }
            systemLogService.sendSystemLogToReportingService("create account", ServiceConstants.systemTriggeredEvent, "strRoles is not null :: admin");
        }
        user.setRoles(roles);
        user.setBalance(50000.00);
        userRepository.save(user);
        systemLogService.sendSystemLogToReportingService("register user", ServiceConstants.systemTriggeredEvent, ServiceConstants.successUserCreation + " user_id: " + user.getID());
        Map<?, ?> response = Map.of("status",ServiceConstants.successStatus, "message", ServiceConstants.successUserCreation,"data", new UserCreationResponse(user.getID(), user.getName(),user.getEmail(),user.getBalance()));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);

    }
}
