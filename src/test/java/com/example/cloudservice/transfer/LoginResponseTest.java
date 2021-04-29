package com.example.cloudservice.transfer;

import com.example.cloudservice.transfer.login.LoginResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
class LoginResponseTest {

    @Autowired
    private JacksonTester<LoginResponse> jacksonTester;

    @Test
    void test_serialization() throws IOException {
        final LoginResponse loginResponse = new LoginResponse("Test token");
        final JsonContent<LoginResponse> result = this.jacksonTester.write(loginResponse);

        final String expectedJson = "{\"auth-token\":\"Test token\"}";
        assertEquals(expectedJson, result.getJson());
    }
}