package com.example.cloudservice.service;

import com.example.cloudservice.model.LoggedOutToken;
import com.example.cloudservice.repository.LoggedOutTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;

import java.time.Instant;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LoggedOutTokenServiceTest {

    private static final String CACHE_NAME = "loggedOutTokens";

    @Value("${app.security.jwt.invalid-token-store-time-millis}")
    private long storeTime;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private LoggedOutTokenRepository tokenRepository;

    @Autowired
    private LoggedOutTokenService tokenService;

    @BeforeEach
    void clear() {
        tokenRepository.deleteAll();
        cacheManager.getCache(CACHE_NAME).invalidate();
    }

    @Test
    void createLoggedOutToken_expectedBehaviour() {
        final String token = "test token";
        final Instant instant = Instant.now();
        final Date expectedStoreTill = Date.from(instant.plusMillis(storeTime));

        final LoggedOutToken actual = tokenService.createLoggedOutToken(token, instant);
        assertNotNull(actual);
        assertEquals(token, actual.getToken());
        assertEquals(expectedStoreTill, actual.getStoreTill());
    }

    @Test
    void addToken_savesNewLoggedOutTokenToDB() {
        final String token = "test token";
        tokenService.addToken(token);
        assertTrue(tokenRepository.existsById(token));
    }

    @Test
    void checkToken_returnsTrue_ifTokenIsPresent() {
        final String token = "test token";
        final Date storeTill = Date.from(Instant.now().plusMillis(storeTime));
        final LoggedOutToken loggedOutToken = new LoggedOutToken(token, storeTill);

        tokenRepository.saveAndFlush(loggedOutToken);
        assertTrue(tokenService.checkToken(token));
    }

    @Test
    void checkToken_returnsFalse_ifTokenIsNotPresent() {
        final String token = "test token";

        assertFalse(tokenRepository.existsById(token));
        assertFalse(tokenService.checkToken(token));
    }

    @Test
    void deleteOldTokens() {
        final String token = "test token";
        final LoggedOutToken actual = new LoggedOutToken(token, Date.from(Instant.now().minusMillis(1)));

        tokenRepository.saveAndFlush(actual);
        assertTrue(tokenRepository.existsById(token));

        tokenService.deleteOldTokens();
        assertFalse(tokenRepository.existsById(token));
    }
}