package client.model.cryptography;

public class Cryptography {

    private String publicKey;
    private String privateKey;
    private String sessionKey;

    public Cryptography() {
        this.publicKey = new String("aaaaaaaaaa");
        this.privateKey = new String("bbbbbbbbbb");
    }

    public String getPublicKey() {
       return this.publicKey;
    }

    public String getPrivateKey() {
        return this.privateKey;
    }

    public String getSessionKey() {
        return this.sessionKey;
    }

    public String generateSessionKey() {
        this.sessionKey = new String("eeeeeeeeee");
        return this.sessionKey;
    }

    public String encodeSessionKey() {    //zwraca klucz sesji zakodowany kluczem prywatnym
        String encodedSessionKey = new String();

        return encodedSessionKey;
    }
}
