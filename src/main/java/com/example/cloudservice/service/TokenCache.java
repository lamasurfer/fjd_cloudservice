package com.example.cloudservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenCache {

    private static final Logger LOGGER = LoggerFactory.getLogger(TokenCache.class);
    private final Map<String, Long> tokenCache = new ConcurrentHashMap<>(100);

    @Value("${app.security.jwt.invalid-token-store-time-millis}")
    private long tokenStoreTime;

    public void addToken(String token) {
        tokenCache.put(token, Date.from(Instant.now().plusMillis(tokenStoreTime)).getTime());
        System.out.println(tokenCache);
    }

    public boolean checkToken(String token) {
        return tokenCache.containsKey(token);
    }

    @Scheduled(fixedRateString = "${app.security.jwt.cache-reset-rate-millis}")
    public void deleteOldTokens() {
        System.out.println(tokenCache);
        Long now = Date.from(Instant.now()).getTime();
        System.out.println(now);
        boolean removed = tokenCache.entrySet().removeIf(entry -> entry.getValue().compareTo(now) < 0);
        String message = "Logged out tokens check performed";
        if (removed) {
            message = message + ", old tokens removed";
        }
        LOGGER.info(message);
        System.out.println(tokenCache);
    }
}
