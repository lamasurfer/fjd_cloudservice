package com.example.cloudservice.service;

import com.example.cloudservice.config.AppConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenCache {

    private static final Logger LOGGER = LoggerFactory.getLogger(TokenCache.class);
    private final Map<String, Long> tokenCache = new ConcurrentHashMap<>(100);

    // TODO поменять время действия
    public void addToken(String token) {
        tokenCache.put(token, Instant.now().getEpochSecond() + 600 + 60); // 11 минут, на 1 мин больше жизни токена
    }

    public boolean checkToken(String token) {
        return tokenCache.containsKey(token);
    }

    // пока каждые 5 минут
    @Scheduled(fixedRate = AppConstants.DELETE_TOKENS_RATE)
    public void deleteOldTokens() {
        Long now = Instant.now().getEpochSecond();
        boolean removed = tokenCache.entrySet().removeIf(entry -> entry.getValue().compareTo(now) < 0);
        String message = "Logged out tokens check performed";
        if (removed) {
            message = message + ", old tokens removed";
        }
        LOGGER.info(message);
    }
}
