package com.example.cloudservice.security;

import com.example.cloudservice.service.LoggedOutTokenService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoggedOutTokenValidatorTest {

    private final Jwt jwt = new Jwt("test token",
            Instant.now(),
            Instant.now().plusSeconds(1),
            Map.of("kid", "1"),
            Map.of("iss", "example.com"));

    @Mock
    private LoggedOutTokenService tokenService;

    @InjectMocks
    private LoggedOutTokenValidator tokenValidator;

    @Test
    void test_validate_callsTokenService_toCheckToken() {
        final String token = jwt.getTokenValue();
        tokenValidator.validate(jwt);

        verify(tokenService).checkToken(token);
    }

    @Test
    void test_validate_returnsNoErrors_ifTokenIsNotLoggedOut() {
        final String token = jwt.getTokenValue();
        when(tokenService.checkToken(token)).thenReturn(false);
        final OAuth2TokenValidatorResult result = tokenValidator.validate(jwt);

        assertFalse(result.hasErrors());
    }

    @Test
    void test_validate_returnsErrors_ifTokenIsLoggedOut() {
        final String token = jwt.getTokenValue();
        when(tokenService.checkToken(token)).thenReturn(true);
        final OAuth2TokenValidatorResult result = tokenValidator.validate(jwt);

        assertTrue(result.hasErrors());
    }
}