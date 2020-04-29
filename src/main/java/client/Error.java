package client;

public class Error extends Exception {

    private String message;


    public Error(String msg) {

        message = new String(msg);
    }


    public String getMessage() {

        return this.message;
    }
}
