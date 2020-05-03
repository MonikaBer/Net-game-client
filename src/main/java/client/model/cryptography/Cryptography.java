package client.model.cryptography;

import javax.annotation.processing.AbstractProcessor;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;

public class Cryptography {

    private PublicKey publicKey;
    private PrivateKey privateKey;
    private SecretKey sessionKey;

    public Cryptography() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(256);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        this.publicKey = keyPair.getPublic();
        this.privateKey = keyPair.getPrivate();
    }

    public byte[] getPublicKey() {
       return this.publicKey.getEncoded();
    }

    public void generateSessionKey() throws NoSuchAlgorithmException {
        SecureRandom secureRandom = new SecureRandom();
        KeyGenerator keyGenerator = KeyGenerator.getInstance("RSA");
        keyGenerator.init(256, secureRandom);
        this.sessionKey = keyGenerator.generateKey();
    }

    //zwraca klucz sesji zakodowany kluczem prywatnym klienta
    public byte[] encryptSessionKey() throws NoSuchPaddingException, NoSuchAlgorithmException,
            BadPaddingException, IllegalBlockSizeException, InvalidKeyException {

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, this.privateKey);
        byte[] cipherSessionKey = cipher.doFinal(this.sessionKey.getEncoded());
        return cipherSessionKey;
    }

    public byte[] encrypt(byte[] plainText) throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidKeyException, BadPaddingException, IllegalBlockSizeException {

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, this.sessionKey);
        byte[] cipherText = cipher.doFinal(plainText);
        return cipherText;
    }

    public byte[] decrypt(byte[] cipherText) throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidKeyException, BadPaddingException, IllegalBlockSizeException {

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, this.sessionKey);
        byte[] plainText = cipher.doFinal(cipherText);
        return plainText;
    }
}
