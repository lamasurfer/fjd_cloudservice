package com.example.cloudservice.security;

import com.example.cloudservice.service.TokenCache;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class LoggedOutTokenValidator implements OAuth2TokenValidator<Jwt> {

    private final OAuth2Error error = new OAuth2Error(OAuth2ErrorCodes.INVALID_TOKEN,
            "User logged out, login to get new token", null);
    private final TokenCache tokenCache;

    public LoggedOutTokenValidator(TokenCache tokenCache) {
        this.tokenCache = tokenCache;
    }

    @Override
    public OAuth2TokenValidatorResult validate(Jwt jwtToken) {
        String token = jwtToken.getTokenValue();
        if (tokenCache.checkToken(token)) {
            return OAuth2TokenValidatorResult.failure(error);
        }
        return OAuth2TokenValidatorResult.success();
    }
}
