package client.model.network.packets.gamerMoving;


public class GamerMoving {

    private int movingDirection;
    private int previousGameLayoutNr;

    public GamerMoving(int movingDirection, int previousGameLayoutNr) {
        this.movingDirection = movingDirection;
        this.previousGameLayoutNr = previousGameLayoutNr;
    }

    public byte[] toBytes() {
        byte[] bytes = new byte[4];

        bytes[0] = (byte)('T');
        bytes[1] = (byte)movingDirection;

        bytes[2] = (byte)(this.previousGameLayoutNr >> 8);
        bytes[3] = (byte)(this.previousGameLayoutNr & 0x0f);

        return bytes;
    }
}
