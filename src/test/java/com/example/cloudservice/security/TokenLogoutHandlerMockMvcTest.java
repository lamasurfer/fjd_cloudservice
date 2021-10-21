package com.example.cloudservice.security;

import com.example.cloudservice.transfer.login.LoginResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TokenLogoutHandlerMockMvcTest {

    private static final String AUTH_TOKEN = "auth-token";
    private static final String BEARER = "Bearer ";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void test_logout_validTokenCannotBeReusedAfterLogout() throws Exception {
        final MvcResult result = this.mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "  \"login\": \"user\",\n" +
                                "  \"password\": \"user\"\n" +
                                "}"))
                .andExpect(status().isOk()).andReturn();


        final ObjectMapper objectMapper = new ObjectMapper();
        final LoginResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), LoginResponse.class);

        final String token = response.getToken();

        this.mockMvc.perform(post("/logout")
                        .header(AUTH_TOKEN, BEARER + token))
                .andExpect(status().isOk());

        this.mockMvc.perform(get("/list")
                        .header(AUTH_TOKEN, BEARER + token))
                .andExpect(status().isUnauthorized())
                .andExpect(content().json("{\"message\":\"Токен доступа недействителен, " +
                        "отозван или поврежден\", \"id\":401}"));
    }
}
