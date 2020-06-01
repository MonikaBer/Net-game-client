package client.model.helpers;


public class Helper {

    public static int getNumberFromBuffer(byte[] bytes, int from, int to) {
        int value = Byte.toUnsignedInt(bytes[from]);
        for (int i = from+1; i < to+1; i++) {
            value = value * 256 + Byte.toUnsignedInt(bytes[i]);
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
