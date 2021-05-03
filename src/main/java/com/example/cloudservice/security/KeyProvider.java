package com.example.cloudservice.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

@Component
public class KeyProvider {

    private final static Logger LOGGER = LoggerFactory.getLogger(KeyProvider.class);

    @Value("${app.security.key-provider.signature-algorithm}")
    private String algorithm;
    @Value("${app.security.key-provider.key-size}")
    private int keySize;
    @Value("${app.security.key-provider.public-key-file-name}")
    private String publicKeyFileName;
    @Value("${app.security.key-provider.private-key-file-name}")
    private String privateKeyFileName;

    public KeyPair provideKeys() {
        KeyPair keyPair;
        if (checkIfKeyFilesExist()) {
            keyPair = loadKeyPair();
        } else {
            keyPair = generateKeyPair();
            saveKeyPair(keyPair);
        }
        return keyPair;
    }

    KeyPair generateKeyPair() {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance(algorithm);
            kpg.initialize(keySize);
            KeyPair keyPair = kpg.generateKeyPair();
            LOGGER.info("KeyPair generated successfully.");
            return keyPair;
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("Failed to generate KeyPair : " + e.getClass().getSimpleName() + ".");
        }
        throw new IllegalStateException("KeyPair generation failed!");
    }

    void saveKeyPair(KeyPair keyPair) {
        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();

        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(
                publicKey.getEncoded());
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(
                privateKey.getEncoded());
        try (FileOutputStream publicFos = new FileOutputStream(publicKeyFileName);
             FileOutputStream privateFos = new FileOutputStream(privateKeyFileName)) {
            publicFos.write(x509EncodedKeySpec.getEncoded());
            privateFos.write(pkcs8EncodedKeySpec.getEncoded());
            LOGGER.info("Keys saved successfully.");
        } catch (IOException e) {
            LOGGER.error("Failed to save keys : " + e.getClass().getSimpleName() + ".");
        }
    }

    KeyPair loadKeyPair() {
        final File publicKeyFile = new File(publicKeyFileName);
        final File privateKeyFile = new File(privateKeyFileName);

        byte[] encodedPublicKey = new byte[(int) publicKeyFile.length()];
        byte[] encodedPrivateKey = new byte[(int) privateKeyFile.length()];

        try (FileInputStream publicFis = new FileInputStream(publicKeyFile);
             FileInputStream privateFis = new FileInputStream(privateKeyFile)) {

            publicFis.read(encodedPublicKey);
            privateFis.read(encodedPrivateKey);

            KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(encodedPublicKey);
            PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(encodedPrivateKey);
            PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
            KeyPair keyPair = new KeyPair(publicKey, privateKey);
            LOGGER.info("KeysPair loaded successfully.");
            return keyPair;

        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            LOGGER.error("Failed to load KeyPair : " + e.getClass().getSimpleName() + ", new KeyPair will be generated.");
            KeyPair keyPair = generateKeyPair();
            saveKeyPair(keyPair);
            return keyPair;
        }
    }

    boolean checkIfKeyFilesExist() {
        final File publicKeyFile = new File(publicKeyFileName);
        final File privateKeyFile = new File(privateKeyFileName);
        return publicKeyFile.exists() && privateKeyFile.exists();
    }

    KeyProvider setPublicKeyFileName(String publicKeyFileName) {
        this.publicKeyFileName = publicKeyFileName;
        return this;
    }

    KeyProvider setPrivateKeyFileName(String privateKeyFileName) {
        this.privateKeyFileName = privateKeyFileName;
        return this;
    }

    KeyProvider setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
        return this;
    }

    KeyProvider setKeySize(int keySize) {
        this.keySize = keySize;
        return this;
    }
}
