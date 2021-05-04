package com.example.cloudservice.validation;

import com.example.cloudservice.transfer.file.RenameRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class RenameRequestValidationTest {

    @Autowired
    private Validator validator;

    @Test
    void test_validRenameRequest_noViolations() {
        final RenameRequest loginRequest = new RenameRequest("test.file");
        final Set<ConstraintViolation<RenameRequest>> violations = validator.validate(loginRequest);

        assertTrue(violations.isEmpty());
    }

    @Test
    void test_emptyFilename_oneViolation() {
        final RenameRequest loginRequest = new RenameRequest("");
        final Set<ConstraintViolation<RenameRequest>> violations = validator.validate(loginRequest);

        assertEquals(1, violations.size());
    }

    @Test
    void test_blankFilename_oneViolation() {
        final RenameRequest loginRequest = new RenameRequest("  ");
        final Set<ConstraintViolation<RenameRequest>> violations = validator.validate(loginRequest);

        assertEquals(1, violations.size());
    }

    @Test
    void test_nullFilename_oneViolation() {
        final RenameRequest loginRequest = new RenameRequest(null);
        final Set<ConstraintViolation<RenameRequest>> violations = validator.validate(loginRequest);

        assertEquals(1, violations.size());
    }
}
