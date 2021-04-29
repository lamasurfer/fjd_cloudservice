package com.example.cloudservice.security;

import com.example.cloudservice.config.AppConstants;
import com.example.cloudservice.service.TokenCache;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class TokenLogoutHandler extends SecurityContextLogoutHandler {

    private final TokenCache tokenCache;

    public TokenLogoutHandler(TokenCache tokenCache) {
        this.tokenCache = tokenCache;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        super.logout(request, response, authentication);
        String bearer = request.getHeader(AppConstants.TOKEN_HEADER);
        if (bearer != null) {
            String token = bearer.substring(AppConstants.TOKEN_START_INDEX);
            tokenCache.addToken(token);
        }
    }
}
