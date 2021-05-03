package com.example.cloudservice.repository;

import com.example.cloudservice.model.LoggedOutToken;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class LoggedOutTokenRepositoryTest {

    @Autowired
    private LoggedOutTokenRepository tokenRepository;

    @Test
    @Transactional
    void removeAllByStoreTillBefore_expectedBehaviour() {
        final Instant instant = Instant.now();
        final String token1 = "test token1";
        final Date storeTill1 = Date.from(instant.minusMillis(1));
        final LoggedOutToken loggedOutToken1 = new LoggedOutToken(token1, storeTill1);
        final String token2 = "test token2";
        final Date storeTill2 = Date.from(instant.plusMillis(1));
        final LoggedOutToken loggedOutToken2 = new LoggedOutToken(token2, storeTill2);

        tokenRepository.saveAndFlush(loggedOutToken1);
        tokenRepository.saveAndFlush(loggedOutToken2);
        assertTrue(tokenRepository.existsById(token1));
        assertTrue(tokenRepository.existsById(token2));

        tokenRepository.removeAllByStoreTillBefore(Date.from(instant));

        assertFalse(tokenRepository.existsById(token1));
        assertTrue(tokenRepository.existsById(token2));
    }
}