package com.example.cloudservice.controller;

import com.example.cloudservice.model.Authority;
import com.example.cloudservice.model.User;
import com.example.cloudservice.repository.UserRepository;
import com.example.cloudservice.security.TokenProvider;
import com.nimbusds.jose.JOSEException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class FileControllerTest {

    private static final String AUTH_TOKEN = "auth-token";
    private final User user = new User().setUsername("john")
            .setPassword("$2a$10$DCRnMsY0JEun9SfNFNBVIurkfiPWz6rKwefuQg5/TbNuRlbfkkC5S")
            .setEnabled(true)
            .setAuthorities(Set.of(new Authority("files")));

    private String token;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenProvider tokenProvider;

    @MockBean
    private UserRepository userRepository;

    @BeforeEach
    void setup() throws JOSEException {
        when(userRepository.findById("john")).thenReturn(Optional.of(user));
        if (token == null) {
            token = "Bearer " + tokenProvider.createToken(user);
        }
    }

    @Test
    void test_uploadFile_noToken_unauthorized() throws Exception {
        this.mockMvc.perform(post("/file"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().json("{\"message\":\"Токен доступа недействителен, отозван или поврежден\", \"id\":401}"));
    }

    @Test
    void test_deleteFile_noToken_unauthorized() throws Exception {
        this.mockMvc.perform(delete("/file"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().json("{\"message\":\"Токен доступа недействителен, отозван или поврежден\", \"id\":401}"));
    }

    @Test
    void test_downLoadFile_noToken_unauthorized() throws Exception {
        this.mockMvc.perform(get("/file"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().json("{\"message\":\"Токен доступа недействителен, отозван или поврежден\", \"id\":401}"));
    }

    @Test
    void test_renameFile_noToken_unauthorized() throws Exception {
        this.mockMvc.perform(put("/file"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().json("{\"message\":\"Токен доступа недействителен, отозван или поврежден\", \"id\":401}"));
    }

    @Test
    void test_listFiles_noToken_unauthorized() throws Exception {
        this.mockMvc.perform(put("/list"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().json("{\"message\":\"Токен доступа недействителен, отозван или поврежден\", \"id\":401}"));
    }

    @Test
    void test_uploadFile_badRequest() throws Exception {
        this.mockMvc.perform(post("/file")
                        .header(AUTH_TOKEN, token))
                .andExpect(status().is4xxClientError())
                .andExpect(content().json("{\"message\":\"Некорректные параметры запроса\", \"id\":400}"));
    }

    @Test
    void test_deleteFile_badRequest() throws Exception {
        this.mockMvc.perform(delete("/file")
                        .header(AUTH_TOKEN, token))
                .andExpect(status().is4xxClientError())
                .andExpect(content().json("{\"message\":\"Не удалось удалить файл! Имя файла не указано\", \"id\":400}"));
    }

    @Test
    void test_downloadFile_badRequest() throws Exception {
        this.mockMvc.perform(get("/file")
                        .header(AUTH_TOKEN, token))
                .andExpect(status().is4xxClientError())
                .andExpect(content().json("{\"message\":\"Не удалось загрузить файл! Имя файла не указано\", \"id\":400}"));
    }

    @Test
    void test_renameFile_badRequest() throws Exception {
        this.mockMvc.perform(put("/file")
                        .header(AUTH_TOKEN, token))
                .andExpect(status().is4xxClientError())
                .andExpect(content().json("{\"message\":\"Некорректные параметры запроса\", \"id\":400}"));
    }

    @Test
    void test_login_wrongMethod_isBadRequest() throws Exception {
        this.mockMvc.perform(post("/list")
                        .header(AUTH_TOKEN, token))
                .andExpect(status().is4xxClientError())
                .andExpect(content().json("{\"message\":\"Http метод не поддерживается\", \"id\":405}"));
    }
}