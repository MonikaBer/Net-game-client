package client.model.network.packets.gamerAction;

import client.model.helpers.Helper;

public class GamerShot {

    private int shotAngle;

    public GamerShot(int shotAngle) {
        this.shotAngle = shotAngle;
    }

    public byte[] toBytes() {
        byte[] bytes = new byte[2];

        bytes[0] = (byte) ('Z');
        bytes[1] = (byte) this.shotAngle;

        byte[] newBytes = Helper.getNewBufferWithCrc(bytes);
        return newBytes;
    }
}
