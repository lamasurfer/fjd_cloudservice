package com.example.cloudservice.service;

import com.example.cloudservice.model.Authority;
import com.example.cloudservice.model.User;
import com.example.cloudservice.repository.UserRepository;
import com.example.cloudservice.security.TokenProvider;
import com.example.cloudservice.transfer.login.LoginRequest;
import com.example.cloudservice.transfer.login.LoginResponse;
import com.nimbusds.jose.JOSEException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest
class LoginServiceTest {

    private final String username = "user";
    private final String password = "password";
    private final String encodedPassword = "$2a$10$DCRnMsY0JEun9SfNFNBVIurkfiPWz6rKwefuQg5/TbNuRlbfkkC5S";

    private final Authority authority = new Authority("files");

    private final User user = new User()
            .setUsername(username)
            .setPassword(encodedPassword)
            .setEnabled(true)
            .setAuthorities(Set.of(authority));

    @MockBean
    private TokenProvider tokenProvider;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private LoginService loginService;

    @Test
    void test_login_expectedBehaviour() throws JOSEException {
        when(tokenProvider.createToken(user)).thenReturn("test_token");
        when(userRepository.findById(username)).thenReturn(Optional.of(user));

        final LoginRequest loginRequest = new LoginRequest(username, password);
        final ResponseEntity<Object> expected = ResponseEntity.ok().body(new LoginResponse("test_token"));

        assertEquals(expected, loginService.login(loginRequest));
    }

    @Test
    void test_loginWrongPassword_throwsException() {
        when(userRepository.findById(username)).thenReturn(Optional.of(user));
        final LoginRequest loginRequest = new LoginRequest(username, "wrong");

        assertThrows(BadCredentialsException.class, () -> loginService.login(loginRequest));
    }

    @Test
    void test_loginWrongUsernameAndPassword_throwsException() {
        when(userRepository.findById(username)).thenReturn(Optional.empty());
        final LoginRequest loginRequest = new LoginRequest("wrong", "wrong");

        assertThrows(BadCredentialsException.class, () -> loginService.login(loginRequest));
    }
}