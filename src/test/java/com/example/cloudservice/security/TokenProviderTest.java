package com.example.cloudservice.security;

import com.example.cloudservice.model.Authority;
import com.example.cloudservice.model.User;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;

import java.security.KeyPair;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
class TokenProviderTest {

    private final Authority scope = new Authority("SCOPE_file");
    private final User user = new User().setUsername("user")
            .setPassword("password")
            .setAuthorities(Set.of(scope));
    private final String issuer = "example.com";

    @Autowired
    private KeyPair keyPair;

    @Autowired
    private JwtDecoder jwtDecoder;

    @Autowired
    private TokenProvider tokenProvider;

    @Test
    void createTokenWithDetails_expectedBehaviour() throws JOSEException {
        final String scopeClaimName = "scp";
        final Instant now = Instant.now();
        final long expirationTimeInMillis = 600000;
        final String algorithm = "RS512";
        final String id = UUID.randomUUID().toString();
        final JWSSigner signer = new RSASSASigner(keyPair.getPrivate());

        final String token = tokenProvider.createToken(user,
                issuer,
                scopeClaimName,
                now,
                expirationTimeInMillis,
                algorithm,
                id,
                signer);

        final OAuth2TokenValidator<Jwt> validator = JwtValidators.createDefaultWithIssuer(issuer);
        final OAuth2TokenValidatorResult result = validator.validate(jwtDecoder.decode(token));

        assertFalse(result.hasErrors());

        final Jwt jwt = jwtDecoder.decode(token);

        assertEquals(now.getEpochSecond(), jwt.getIssuedAt().getEpochSecond());
        assertEquals(now.plusMillis(expirationTimeInMillis).getEpochSecond(), jwt.getExpiresAt().getEpochSecond());
        assertEquals(user.getUsername(), jwt.getSubject());
        assertEquals(issuer, jwt.getClaim("iss"));
        assertEquals(scope.getAuthority(), jwt.getClaim("scp"));
        assertEquals(id, jwt.getHeaders().get("kid"));
        assertEquals(algorithm, jwt.getHeaders().get("alg"));
    }

    @Test
    void createTokenSimple_expectedBehaviour() throws JOSEException {
        final String token = tokenProvider.createToken(user);

        final OAuth2TokenValidator<Jwt> validator = JwtValidators.createDefaultWithIssuer(issuer);
        final OAuth2TokenValidatorResult result = validator.validate(jwtDecoder.decode(token));

        assertFalse(result.hasErrors());
    }
}