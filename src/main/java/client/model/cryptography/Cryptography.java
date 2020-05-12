package client.model.cryptography;

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

    //return session key encrypted by server public key
    public byte[] encryptSessionKey(byte[] servPubKey) throws NoSuchPaddingException, NoSuchAlgorithmException,
            BadPaddingException, IllegalBlockSizeException, InvalidKeyException {

        SecretKey serverPublicKey = new SecretKeySpec(servPubKey, 0, servPubKey.length, "RSA");
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, serverPublicKey);
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
