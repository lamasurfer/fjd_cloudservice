package com.example.cloudservice.controller;

import com.example.cloudservice.service.LoginService;
import com.example.cloudservice.transfer.login.LoginRequest;
import com.example.cloudservice.transfer.login.LoginResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LoginService loginService;

    @Test
    void test_login_wrongMethod_isBadRequest() throws Exception {
        this.mockMvc.perform(get("/login"))
                .andExpect(status().is4xxClientError())
                .andExpect(content().json("{\"message\":\"Http метод не поддерживается\", \"id\":405}"));
    }

    @Test
    void test_login_noJson_isBadRequest() throws Exception {
        this.mockMvc.perform(post("/login"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"message\":\"Некорректный запрос\", \"id\":400}"));
    }

    @Test
    void test_login_emptyJson_isBadRequest() throws Exception {
        this.mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"message\":\"Некорректный запрос\", \"id\":400}"));
    }

    @Test
    void test_login_loginRequestWrongJson_isBadRequest() throws Exception {
        this.mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \": \"john\"\n" +
                        "}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"message\":\"Некорректный запрос\", \"id\":400}"));
    }

    @Test
    void test_login_loginRequestEmptyLogin_isBadRequest() throws Exception {
        this.mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"login\": \"\",\n" +
                        "  \"password\": \"john\"\n" +
                        "}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"message\":\"Логин должен быть заполнен\", \"id\":400}"));
    }

    @Test
    void test_login_loginRequestEmptyPassword_isBadRequest() throws Exception {
        this.mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"login\": \"john\",\n" +
                        "  \"password\": \"\"\n" +
                        "}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"message\":\"Пароль должен быть заполнен\", \"id\":400}"));
    }

    @Test
    void test_login_loginRequestBothEmpty_isBadRequest() throws Exception {
        this.mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"login\": \"\",\n" +
                        "  \"password\": \"\"\n" +
                        "}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"message\":\"Логин должен быть заполнен, " +
                        "Пароль должен быть заполнен\", \"id\":400}"));
    }

    @Test
    void test_login_validLoginRequest_isOk() throws Exception {
        final String authToken = "test token";
        final LoginResponse loginResponse = new LoginResponse(authToken);
        final ResponseEntity<Object> responseEntity = ResponseEntity.ok().body(loginResponse);

        when(loginService.login(any(LoginRequest.class))).thenReturn(responseEntity);

        this.mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"login\": \"john\",\n" +
                        "  \"password\": \"john\"\n" +
                        "}"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"auth-token\":\"" + authToken + "\"}"));
    }

    @Test
    void test_login_badLoginRequest_unauthorized() throws Exception {
        when(loginService.login(any(LoginRequest.class))).thenThrow(BadCredentialsException.class);

        this.mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"login\": \"wrong\",\n" +
                        "  \"password\": \"wrong\"\n" +
                        "}"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().json("{\"message\":\"Пользователь с таким логином и паролем не найден\", \"id\":401}"));
    }
}