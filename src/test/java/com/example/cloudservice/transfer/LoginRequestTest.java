package com.example.cloudservice.transfer;

import com.example.cloudservice.transfer.login.LoginRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
class LoginRequestTest {

    @Autowired
    private JacksonTester<LoginRequest> jacksonTester;

    @Test
    void test_deserialization() throws IOException {
        final String json = "{\n" +
                "  \"login\": \"login\",\n" +
                "  \"password\": \"password\"\n" +
                "}";

        final LoginRequest loginRequest = jacksonTester.parseObject(json);
        assertEquals("login", loginRequest.getLogin());
        assertEquals("password", loginRequest.getPassword());
    }
}