package com.example.cloudservice.security;

import com.example.cloudservice.service.LoggedOutTokenService;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class TokenLogoutHandler extends SecurityContextLogoutHandler {

    public static final int TOKEN_START_INDEX = 7;

    private final LoggedOutTokenService loggedOutTokenService;

    public TokenLogoutHandler(LoggedOutTokenService loggedOutTokenService) {
        this.loggedOutTokenService = loggedOutTokenService;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        super.logout(request, response, authentication);
        final String bearer = request.getHeader(SecurityConfig.AUTH_TOKEN);
        if (bearer != null) {
            final String token = bearer.substring(TOKEN_START_INDEX);
            loggedOutTokenService.addToken(token);
        }
    }
}
