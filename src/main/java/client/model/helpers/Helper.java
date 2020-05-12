package client.model.helpers;

import java.nio.ByteBuffer;

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

    public static int getFromBuffer(byte[] bytes, int from, int to) {
//        ByteBuffer tempBuffer = ByteBuffer.wrap(bytes, from, to-from+1);
//        int value = tempBuffer.getInt();
        int value = Byte.valueOf(bytes[from]).intValue();
        for (int i = from+1; i < to+1; i++) {
            value = value * 256 + Byte.valueOf(bytes[i]).intValue();
        }
        return value;
    }

    public static boolean ifStartPacket(byte[] bytes) {
        char tag = (char)bytes[0];
        if (tag != 'S')
            return false;
        return true;
    }

    public static boolean ifStopPacket(byte[] bytes) {
        char tag = (char)bytes[0];
        if (tag != 'E')
            return false;
        return true;
    }
}
