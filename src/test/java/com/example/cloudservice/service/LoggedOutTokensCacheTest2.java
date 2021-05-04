package com.example.cloudservice.service;

import com.example.cloudservice.repository.LoggedOutTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class LoggedOutTokensCacheTest2 {

    private static final String CACHE_NAME = "loggedOutTokens";

    @Autowired
    private CacheManager cacheManager;

    @MockBean
    private LoggedOutTokenRepository tokenRepository;

    @Autowired
    private LoggedOutTokenService tokenService;

    @BeforeEach
    void setUp() {
        Objects.requireNonNull(cacheManager.getCache(CACHE_NAME)).invalidate();
    }

    @Test
    void test_checkToken_addsTokenToCache() {
        final String token = "test token1";
        when(tokenRepository.existsById(token)).thenReturn(true);

        tokenService.checkToken(token);
        final Cache cache = cacheManager.getCache(CACHE_NAME);

        assertNotNull(cache);
        assertTrue((boolean) cache.get(token).get());
    }

    @Test
    void test_addToken_evictsTokensCache() {

        final String token = "test token1";
        when(tokenRepository.existsById(token)).thenReturn(true);
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

        when(tokenRepository.existsById(token1)).thenReturn(true);
        when(tokenRepository.existsById(token2)).thenReturn(true);

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

    @Test
    void test_checkToken_twoChecks_onlyOneCallToDB() {
        final String token = "test token";
        when(tokenRepository.existsById(token)).thenReturn(true);

        tokenService.checkToken(token); // call
        tokenService.checkToken(token); // cache

        verify(tokenRepository, atMostOnce()).existsById(token);
    }

    @Test
    void test_addToken_resetsCache() {
        final String token = "test token";
        when(tokenRepository.existsById(token)).thenReturn(true);

        tokenService.checkToken(token); // call
        tokenService.checkToken(token); // cache

        verify(tokenRepository, atMostOnce()).existsById(token); // 2 method calls, 1 DB call

        tokenService.addToken(token); // cache reset
        when(tokenRepository.existsById(token)).thenReturn(false);

        tokenService.checkToken(token); // call
        tokenService.checkToken(token); // cache

        verify(tokenRepository, times(2)).existsById(token); // 4 method calls, 2 DB calls total
    }
}
