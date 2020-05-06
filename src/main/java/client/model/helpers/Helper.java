package client.model.helpers;

public class Helper {

    public static String convertToString(byte[] bytes) {
        StringBuilder sBuilder = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            sBuilder.append((char)bytes[i]);
        }
        return sBuilder.toString();
    }

    //TODO: this function is planning to convert to int (not String)
    //'from' and 'to' is a part of 'bytes' to convertion (closed interval)
    public static String convertGamerId(byte[] bytes, int from, int to) {
        StringBuilder sBuilder = new StringBuilder();
        for (int i = from; i < to+1; i++) {
            sBuilder.append((char)bytes[i]);
        }
        return sBuilder.toString();
    }
}
