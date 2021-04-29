package com.example.cloudservice.security;

import com.example.cloudservice.config.AppConstants;
import com.example.cloudservice.model.User;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class TokenProvider {

    private final JWSSigner signer;

    public TokenProvider(KeyPair keyPair) {
        this.signer = new RSASSASigner(keyPair.getPrivate());
    }

    public String createToken(User user) throws JOSEException {
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer(AppConstants.ISSUER)
                .claim(AppConstants.SCOPE_CLAIM_NAME, user.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.joining(",")))
                .expirationTime(Date.from(Instant.now().plusSeconds(600))) // 10 минут TODO поменять
                .build();

        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.RS512).keyID(UUID.randomUUID().toString()).build();

        SignedJWT signedJWT = new SignedJWT(header, claimsSet);
        signedJWT.sign(signer);

        return signedJWT.serialize();
    }
}
