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

    public ResponseEntity<?> authenticateUserLogin(LoginRequest loginRequest){
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
        Map<?, ?> response = Map
                .of("status",ServiceConstants.successStatus,
                        "message",ServiceConstants.userLoginSuccess,
                        "accessToken",jwt,
                        "data", new JwtResponse(userDetails.getId(),userDetails.getName(),userDetails.getEmail(),
                                roles,userDetails.getBalance(), ServiceConstants.successStatus));
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> registerUser(SignupRequest signUpRequest) throws RoleNotFoundException {
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            Map<String,String> hashResponse = Map
                    .of("status",ServiceConstants.failureStatus,"message",ServiceConstants.emailAlreadyTaken);
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
        }
        user.setRoles(roles);
        user.setBalance(50000.00);
        userRepository.save(user);
        Map<?, ?> response = Map.of("status",ServiceConstants.successStatus, "message",ServiceConstants.successUserCreation,"data", new UserCreationResponse(user.getID(), user.getName(),user.getEmail()));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);

    }
}
