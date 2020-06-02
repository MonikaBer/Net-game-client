package client.model.network.packets.gamerAction;


import client.model.helpers.Helper;

public class GamerMoving {

    private int movingDirection;

    public GamerMoving(int movingDirection) {
        this.movingDirection = movingDirection;
    }

    public byte[] toBytes() {
        byte[] bytes = new byte[2];

        bytes[0] = (byte) ('T');
        bytes[1] = (byte) this.movingDirection;

        byte[] newBytes = Helper.getNewBufferWithCrc(bytes);
        return newBytes;
    }
}
