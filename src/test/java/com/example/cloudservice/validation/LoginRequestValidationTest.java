package com.example.cloudservice.validation;

import com.example.cloudservice.transfer.login.LoginRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class LoginRequestValidationTest {

    @Autowired
    private Validator validator;

    @Test
    void test_validLoginRequest_noViolations() {
        final LoginRequest loginRequest = new LoginRequest("login", "password");
        final Set<ConstraintViolation<LoginRequest>> violations = validator.validate(loginRequest);

        assertTrue(violations.isEmpty());
    }

    @Test
    void test_emptyLoginAndPassword_twoViolations() {
        final LoginRequest loginRequest = new LoginRequest("", "");
        final Set<ConstraintViolation<LoginRequest>> violations = validator.validate(loginRequest);

        assertEquals(2, violations.size());
    }

    @Test
    void test_blankLoginAndPassword_twoViolations() {
        final LoginRequest loginRequest = new LoginRequest("  ", "  ");
        final Set<ConstraintViolation<LoginRequest>> violations = validator.validate(loginRequest);

        assertEquals(2, violations.size());
    }

    @Test
    void test_nullLoginAndPassword_twoViolations() {
        final LoginRequest loginRequest = new LoginRequest(null, null);
        final Set<ConstraintViolation<LoginRequest>> violations = validator.validate(loginRequest);

        assertEquals(2, violations.size());
    }
}
