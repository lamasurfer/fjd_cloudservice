package com.example.cloudservice.security;

import com.example.cloudservice.service.LoggedOutTokenService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TokenLogoutHandlerTest extends SecurityContextLogoutHandler {

    @Mock
    private LoggedOutTokenService tokenService;

    @Spy
    @InjectMocks
    private TokenLogoutHandler logoutHandler;

    @Test
    void test_Logout_logoutsAndAddsTokenToLoggedOutTokenService_ifTokenIsPresent() {
        final String headerName = "auth-token";
        final String bearer = "Bearer ";
        final String token = "test_token";
        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(headerName, bearer + token);

        logoutHandler.logout(request, null, null);
        verify(tokenService).addToken(token);
        verify((SecurityContextLogoutHandler) logoutHandler).logout(request, null, null);
    }

    @Test
    void test_Logout_onlyLogouts_ifTokenIsNotPresent() {
        final MockHttpServletRequest request = new MockHttpServletRequest();

        logoutHandler.logout(request, null, null);
        verify(tokenService, never()).addToken(anyString());
        verify((SecurityContextLogoutHandler) logoutHandler).logout(request, null, null);
    }
}