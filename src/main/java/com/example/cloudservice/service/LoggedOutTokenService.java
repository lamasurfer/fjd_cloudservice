package com.example.cloudservice.service;

import com.example.cloudservice.model.LoggedOutToken;
import com.example.cloudservice.repository.LoggedOutTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.Date;

@Service
public class LoggedOutTokenService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggedOutTokenService.class);
    private static final String TOKENS_REMOVED_MESSAGE = "Cache reset performed, present outdated tokens removed from DB";
    private static final String CACHE_NAME = "loggedOutTokens";

    private final LoggedOutTokenRepository tokenRepository;

    @Value("${app.security.jwt.invalid-token-store-time-millis}")
    private long tokenStoreTime;

    public LoggedOutTokenService(LoggedOutTokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    @Transactional
    @CacheEvict(CACHE_NAME)
    public void addToken(String token) {
        final LoggedOutToken loggedOutToken = createLoggedOutToken(token, Instant.now());
        tokenRepository.saveAndFlush(loggedOutToken);
    }

    @Cacheable(CACHE_NAME)
    public boolean checkToken(String token) {
        return tokenRepository.existsById(token);
    }

    @Transactional
    @Scheduled(fixedRateString = "${app.security.jwt.cache-reset-rate-millis}")
    @CacheEvict(value = CACHE_NAME, allEntries = true)
    public void deleteOldTokens() {
        final Date now = Date.from(Instant.now());
        tokenRepository.removeAllByStoreTillBefore(now);
        LOGGER.info(TOKENS_REMOVED_MESSAGE);
    }

    LoggedOutToken createLoggedOutToken(String token, Instant instant) {
        final Date storeTime = Date.from(instant.plusMillis(tokenStoreTime));
        return new LoggedOutToken(token, storeTime);
    }
}
