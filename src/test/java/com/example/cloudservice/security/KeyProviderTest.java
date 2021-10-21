package com.example.cloudservice.security;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.*;

class KeyProviderTest {

    private final String publicKey = "test_public.key";
    private final String privateKey = "test_private.key";
    private final String algorithm = "RSA";
    private final int keySize = 2048;

    private final KeyProvider keyProvider = new KeyProvider()
            .setAlgorithm(algorithm)
            .setKeySize(keySize)
            .setPrivateKeyFileName(privateKey)
            .setPublicKeyFileName(publicKey);

    @Test
    void test_generateKeyPair() throws NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        final KeyPair keyPair = keyProvider.generateKeyPair();

        assertNotNull(keyProvider.generateKeyPair());
        assertTrue(verifyKeys(keyPair));
    }

    @Test
    void test_saveKeys() {
        final KeyPair keyPair = keyProvider.generateKeyPair();

        final File publicFile = new File(publicKey);
        final File privateFile = new File(privateKey);

        keyProvider.saveKeyPair(keyPair);

        assertTrue(publicFile.exists());
        assertTrue(privateFile.exists());

        publicFile.delete();
        privateFile.delete();
    }

    @Test
    void test_loadKeys() throws NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        final KeyPair keyPair = keyProvider.generateKeyPair();

        final RSAPublicKey oldPublicKey = (RSAPublicKey) keyPair.getPublic();
        final RSAPrivateKey oldPrivateKey = (RSAPrivateKey) keyPair.getPrivate();

        final File publicFile = new File(publicKey);
        final File privateFile = new File(privateKey);

        keyProvider.saveKeyPair(keyPair);

        final KeyPair loadedKeyPair = keyProvider.loadKeyPair();

        final RSAPublicKey publicKey = (RSAPublicKey) loadedKeyPair.getPublic();
        final RSAPrivateKey privateKey = (RSAPrivateKey) loadedKeyPair.getPrivate();

        assertTrue(verifyKeys(keyPair));
        assertEquals(oldPublicKey, publicKey);
        assertEquals(oldPrivateKey, privateKey);

        publicFile.delete();
        privateFile.delete();
    }

    @Test
    void test_checkIfKeyFilesExist() throws IOException {
        final File publicFile = new File(publicKey);
        final File privateFile = new File(privateKey);

        publicFile.createNewFile();
        privateFile.createNewFile();

        assertTrue(keyProvider.checkIfKeyFilesExist());

        publicFile.delete();
        privateFile.delete();

        assertFalse(keyProvider.checkIfKeyFilesExist());
    }


    boolean verifyKeys(KeyPair keyPair) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        final RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        final RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

        final byte[] challenge = new byte[10000];
        ThreadLocalRandom.current().nextBytes(challenge);

        final Signature sig = Signature.getInstance("SHA512withRSA");
        sig.initSign(privateKey);
        sig.update(challenge);
        final byte[] signature = sig.sign();

        sig.initVerify(publicKey);
        sig.update(challenge);

        return sig.verify(signature);
    }
}