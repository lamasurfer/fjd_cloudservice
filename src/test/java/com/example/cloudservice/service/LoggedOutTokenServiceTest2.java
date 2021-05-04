package com.example.cloudservice.service;

import com.example.cloudservice.model.LoggedOutToken;
import com.example.cloudservice.repository.LoggedOutTokenRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;

import java.time.Instant;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoggedOutTokenServiceTest2 {

    @Value("${app.security.jwt.invalid-token-store-time-millis}")
    private long storeTime;

    @Mock
    private LoggedOutTokenRepository tokenRepository;

    @InjectMocks
    private LoggedOutTokenService tokenService;

    @Test
    void test_createLoggedOutToken_expectedBehaviour() {
        final String token = "test token";
        final Instant instant = Instant.now();
        final Date expectedStoreTill = Date.from(instant.plusMillis(storeTime));

        final LoggedOutToken actual = tokenService.createLoggedOutToken(token, instant);
        assertNotNull(actual);
        assertEquals(token, actual.getToken());
        assertEquals(expectedStoreTill, actual.getStoreTill());
    }

    @Test
    void test_addToken_savesNewLoggedOutTokenToDB() {
        final String token = "test token";
        tokenService.addToken(token);

        verify(tokenRepository).saveAndFlush(any(LoggedOutToken.class));
    }

    @Test
    void test_checkToken_returnsTrue_ifTokenIsPresent() {
        final String token = "test token";
        when(tokenRepository.existsById(token)).thenReturn(true);

        assertTrue(tokenService.checkToken(token));
    }

    @Test
    void test_checkToken_returnsFalse_ifTokenIsNotPresent() {
        final String token = "test token";
        when(tokenRepository.existsById(token)).thenReturn(false);

        assertFalse(tokenService.checkToken(token));
    }

    @Test
    void test_deleteOldTokens() {
        tokenService.deleteOldTokens();

        verify(tokenRepository).removeAllByStoreTillBefore(any(Date.class));
    }
}