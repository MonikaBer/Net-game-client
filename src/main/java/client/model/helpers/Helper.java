package client.model.helpers;

import java.nio.ByteBuffer;

public class Helper {

    public static int getFromBuffer(byte[] bytes, int from, int to) {
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
