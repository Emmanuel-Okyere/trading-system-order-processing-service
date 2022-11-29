package com.tlc.group.seven.orderprocessingservice.authentication.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.tlc.group.seven.orderprocessingservice.authentication.service.UserDetailsImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);
    @Value("${authentication.app.jwtSecret}")
    private String jwtSecret;

    @Value("${authentication.app.jwtExpirationMs}")
    private Integer jwtExpirationMS;

    public String generateJwtToken(Authentication authentication){
        UserDetailsImpl userPrinciple  =  (UserDetailsImpl) authentication.getPrincipal();
        return JWT.create().withSubject(userPrinciple.getEmail())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + jwtExpirationMS))
                .sign(Algorithm.HMAC512(jwtSecret));
    }
    public boolean validateJwtToken(String jwt) {
        try {
            JWT.create().withJWTId(jwtSecret).withNullClaim(jwt);
            return true;
        } catch (JWTVerificationException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }

    public String getEmailFromJwtToken(String jwt) {
        return JWT.decode(jwt).getSubject();
    }
}