package client.model.network.packets.gamerMoving;

import java.util.zip.CRC32;
import java.util.zip.Checksum;

public class GamerMoving {

    private int movingDirection;
    private int shootAngle;          // 0 - if without shoot

    public GamerMoving(int movingDirection, int shootAngle) {
        this.movingDirection = movingDirection;
        this.shootAngle = shootAngle;
    }

    public byte[] toBytes() {
        byte[] bytes = new byte[6];

        bytes[0] = (byte)movingDirection;
        bytes[1] = (byte)shootAngle;

        Checksum checksum = new CRC32();
        checksum.update(bytes, 0, 2);
        int checksumValue = (int)checksum.getValue();

        byte temp = (byte)(checksumValue >> 24);
        bytes[2] = temp;
        temp = (byte)(checksumValue << 8);
        temp = (byte)(checksumValue >> 24);
        bytes[3] = temp;
        temp = (byte)(checksumValue << 16);
        temp = (byte)(checksumValue >> 24);
        bytes[4] = temp;
        temp = (byte)(checksumValue << 24);
        temp = (byte)(checksumValue >> 24);
        bytes[5] = temp;

        return bytes;
    }
}