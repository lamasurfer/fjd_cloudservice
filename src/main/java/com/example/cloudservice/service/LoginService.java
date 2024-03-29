package com.example.cloudservice.service;

import com.example.cloudservice.model.User;
import com.example.cloudservice.security.TokenProvider;
import com.example.cloudservice.transfer.login.LoginRequest;
import com.example.cloudservice.transfer.login.LoginResponse;
import com.nimbusds.jose.JOSEException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    private final AuthenticationManager manager;
    private final TokenProvider tokenProvider;

    public LoginService(AuthenticationManager manager, TokenProvider tokenProvider) {
        this.manager = manager;
        this.tokenProvider = tokenProvider;
    }

    public ResponseEntity<Object> login(LoginRequest loginRequest) throws JOSEException {
        final String username = loginRequest.getLogin();
        final String password = loginRequest.getPassword();
        final Authentication authentication =
                manager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        final User userEntity = (User) authentication.getPrincipal();
        final String token = tokenProvider.createToken(userEntity);
        return ResponseEntity.ok().body(new LoginResponse(token));
    }
}

