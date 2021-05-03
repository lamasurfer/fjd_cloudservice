package com.example.cloudservice.service;

import com.example.cloudservice.model.LoggedOutToken;
import com.example.cloudservice.repository.LoggedOutTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.time.Instant;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class LoggedOutTokensCacheTest {

    private static final String CACHE_NAME = "loggedOutTokens";

    private final LoggedOutToken loggedOutToken1 = new LoggedOutToken("test token1", Date.from(Instant.now()));
    private final LoggedOutToken loggedOutToken2 = new LoggedOutToken("test token2", Date.from(Instant.now()));

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private LoggedOutTokenRepository tokenRepository;

    @Autowired
    private LoggedOutTokenService tokenService;

    @BeforeEach
    void setUp() {
        tokenRepository.save(loggedOutToken1);
        tokenRepository.save(loggedOutToken2);
        cacheManager.getCache(CACHE_NAME).invalidate();
    }

    @Test
    void test_checkToken_addsTokenToCache() {
        final String token = "test token1";

        tokenService.checkToken(token);
        final Cache cache = cacheManager.getCache(CACHE_NAME);

        assertNotNull(cache);
        assertTrue((boolean) cache.get(token).get());
    }

    @Test
    void test_addToken_evictsTokensCache() {

        final String token = "test token1";

        tokenService.checkToken(token);

        tokenService.addToken(token);
        final Cache cache = cacheManager.getCache(CACHE_NAME);

        assertNotNull(cache);
        assertNull(cache.get(token));
    }

    @Test
    void test_deleteOldTokens_evictsAllTokensCache() {

        final String token1 = "test token1";
        final String token2 = "test token2";

        tokenService.checkToken(token1);
        tokenService.checkToken(token2);

        final Cache cache = cacheManager.getCache(CACHE_NAME);

        assertNotNull(cache);
        assertNotNull(cache.get(token1));
        assertNotNull(cache.get(token2));
        assertTrue((boolean) cache.get(token1).get());
        assertTrue((boolean) cache.get(token2).get());

        tokenService.deleteOldTokens();

        assertNull(cache.get(token1));
        assertNull(cache.get(token2));
    }
}
