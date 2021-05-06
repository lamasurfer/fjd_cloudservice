package com.example.cloudservice;

import com.example.cloudservice.controller.FileController;
import com.example.cloudservice.controller.LoginController;
import com.example.cloudservice.repository.FileRepository;
import com.example.cloudservice.repository.LoggedOutTokenRepository;
import com.example.cloudservice.repository.UserRepository;
import com.example.cloudservice.security.*;
import com.example.cloudservice.service.FileService;
import com.example.cloudservice.service.LoggedOutTokenService;
import com.example.cloudservice.service.LoginService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.support.MessageSourceAccessor;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class CloudServiceApplicationTests {

    @Autowired
    private LoginController loginController;

    @Autowired
    private LoginService loginService;

    @Autowired
    private FileController fileController;

    @Autowired
    private FileService fileService;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private LoggedOutTokenRepository loggedOutTokenRepository;

    @Autowired
    private LoggedOutTokenService loggedOutTokenService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageSourceAccessor messages;

    @Autowired
    private KeyProvider keyProvider;

    @Autowired
    private LoggedOutTokenValidator loggedOutTokenValidator;

    @Autowired
    private MessagingAuthenticationEntryPoint entryPoint;

    @Autowired
    private TokenLogoutHandler tokenLogoutHandler;

    @Autowired
    private TokenProvider tokenProvider;

    @Test
    void contextLoads() {
        assertNotNull(loginController);
        assertNotNull(loginService);
        assertNotNull(fileController);
        assertNotNull(fileService);
        assertNotNull(fileRepository);
        assertNotNull(loggedOutTokenRepository);
        assertNotNull(loggedOutTokenService);
        assertNotNull(userRepository);
        assertNotNull(messages);
        assertNotNull(keyProvider);
        assertNotNull(loggedOutTokenValidator);
        assertNotNull(entryPoint);
        assertNotNull(tokenLogoutHandler);
        assertNotNull(tokenProvider);
    }
}
