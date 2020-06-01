package client.model.network.packets.gamerAction;


public class GamerMoving extends GamerAction {

    private int movingDirection;

    public GamerMoving(int movingDirection, int previousGameLayoutNr) {
        super(previousGameLayoutNr);
        this.movingDirection = movingDirection;
    }

    public byte[] toBytes() {
        byte[] bytes = new byte[4];

        bytes[0] = (byte) ('T');
        bytes[1] = (byte) this.movingDirection;

        bytes[2] = (byte) (this.previousGameLayoutNr >> 8);
        bytes[3] = (byte) (this.previousGameLayoutNr & 0x0f);

        return bytes;
    }
}
