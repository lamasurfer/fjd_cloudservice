package com.example.cloudservice.transfer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
class ErrorMessageTest {

    @Autowired
    private JacksonTester<ErrorMessage> jacksonTester;

    @Test
    void test_serialization() throws IOException {
        final ErrorMessage errorMessage = new ErrorMessage("Test error message", 0);
        final JsonContent<ErrorMessage> result = this.jacksonTester.write(errorMessage);

        final String expectedJson = "{\"message\":\"Test error message\",\"id\":0}";
        assertEquals(expectedJson, result.getJson());
    }
}