package com.example.cloudservice.security;

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

    private String algorithm = "RSA";
    private int keySize = 2048;
    private String publicKeyFileName = "public.key";
    private String privateKeyFileName = "private.key";

    public KeyPair provideKeys() {
        KeyPair kpg;
        if (checkIfKeyFilesExist()) {
            kpg = loadKeyPair();
        } else {
            kpg = generateKeyPair();
            saveKeyPair(kpg);
        }
        return kpg;
    }

    KeyPair generateKeyPair() {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance(algorithm);
            kpg.initialize(keySize);
            return kpg.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        throw new IllegalStateException();
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
        } catch (IOException e) {
            e.printStackTrace();
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

            return new KeyPair(publicKey, privateKey);

        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
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
