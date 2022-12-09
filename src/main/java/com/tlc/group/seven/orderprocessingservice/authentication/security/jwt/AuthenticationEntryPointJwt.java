package com.tlc.group.seven.orderprocessingservice.authentication.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tlc.group.seven.orderprocessingservice.constant.ServiceConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
@Component
public class AuthenticationEntryPointJwt implements AuthenticationEntryPoint {
    Logger logger = LoggerFactory.getLogger(AuthenticationEntryPointJwt.class);

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        logger.error("Unauthorised error: {}",authException.getMessage());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_OK);
        final Map<String, Object> body = new HashMap<>();
        body.put("status", ServiceConstants.failureStatus);
        body.put("message",ServiceConstants.userBadCredential);

        final ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(),body);
    }
}
