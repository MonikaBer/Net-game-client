package client.model.helpers;


import java.util.zip.CRC32;
import java.util.zip.Checksum;

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

    public static byte[] getNewBufferWithCrc(byte[] buffer) {
        byte[] newBuffer = new byte[buffer.length+4];           //additionally 4 bytes of CRC
        Checksum checksum = new CRC32();
        checksum.update(buffer, 0, buffer.length);
        int checksumValue = (int)checksum.getValue();
        for (int i = 0; i < buffer.length; i++) {
            newBuffer[i] = buffer[i];
        }
        newBuffer[buffer.length] = (byte) (checksumValue >> 24);
        newBuffer[buffer.length+1] = (byte) ((checksumValue << 8) >> 24);
        newBuffer[buffer.length+2] = (byte) ((checksumValue << 16 ) >> 24);
        newBuffer[buffer.length+3] = (byte) ((checksumValue << 24) >> 24);
        return newBuffer;
    }
}
