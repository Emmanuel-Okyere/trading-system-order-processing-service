package com.tlc.group.seven.orderprocessingservice.authentication.controller;


import com.tlc.group.seven.orderprocessingservice.authentication.model.ERole;
import com.tlc.group.seven.orderprocessingservice.authentication.model.Role;
import com.tlc.group.seven.orderprocessingservice.authentication.model.User;
import com.tlc.group.seven.orderprocessingservice.authentication.payload.request.LoginRequest;
import com.tlc.group.seven.orderprocessingservice.authentication.payload.request.SignupRequest;
import com.tlc.group.seven.orderprocessingservice.authentication.payload.response.JwtResponse;
import com.tlc.group.seven.orderprocessingservice.authentication.payload.response.MessageResponse;
import com.tlc.group.seven.orderprocessingservice.authentication.repository.RoleRepository;
import com.tlc.group.seven.orderprocessingservice.authentication.repository.UserRepository;
import com.tlc.group.seven.orderprocessingservice.authentication.security.jwt.JwtUtils;
import com.tlc.group.seven.orderprocessingservice.authentication.service.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*",maxAge = 3600)
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
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

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest){
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
        return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getId(),userDetails.getName(),userDetails.getEmail(), roles,userDetails.getBalance()));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            HashMap<String,String> hashResponse = new HashMap<>();
            hashResponse.put("status","failure");
            hashResponse.put("message","email already taken");
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
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin" -> {
                        Role adminRole = roleRepository.findByName(ERole.ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);
                    }
                    default -> {
                        Role userRole = roleRepository.findByName(ERole.USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                    }
                }
            });
        }
        user.setRoles(roles);
        userRepository.save(user);
        HashMap<String,String> userCreatedResponse = new HashMap<>();
        userCreatedResponse.put("status", "success");
        userCreatedResponse.put("message", "user created successfully");
        userCreatedResponse.put("id", user.getID().toString());
        userCreatedResponse.put("name", user.getName());
        userCreatedResponse.put("email", user.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(userCreatedResponse);
    }
}
