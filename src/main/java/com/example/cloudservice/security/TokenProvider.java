package com.example.cloudservice.security;

import com.example.cloudservice.model.User;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class TokenProvider {

    public static final String SCOPE_CLAIM_NAME = "scp";

    private final JWSSigner signer;

    @Value("${app.security.jwt.token-expiration-time-millis}")
    private long tokenExpirationTime;
    @Value("${app.security.jwt.signature-algorithm}")
    private String algorithm;
    @Value("${app.security.jwt.issuer}")
    private String issuer;

    public TokenProvider(KeyPair keyPair) {
        this.signer = new RSASSASigner(keyPair.getPrivate());
    }

    public String createToken(User user) throws JOSEException {
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer(issuer)
                .claim(SCOPE_CLAIM_NAME, user.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.joining(",")))
                .expirationTime(Date.from(Instant.now().plusMillis(tokenExpirationTime)))
                .build();

        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.parse(algorithm)).keyID(UUID.randomUUID().toString()).build();

        SignedJWT signedJWT = new SignedJWT(header, claimsSet);
        signedJWT.sign(signer);

        return signedJWT.serialize();
    }
}
