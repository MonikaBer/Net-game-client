package client.model.network.packets.gamerAction;

public class GamerShot extends GamerAction {

    private int shotAngle;

    public GamerShot(int shotAngle, int previousGameLayoutNr) {
        super(previousGameLayoutNr);
        this.shotAngle = shotAngle;
    }

    public byte[] toBytes() {
        byte[] bytes = new byte[4];

        bytes[0] = (byte) ('Z');
        bytes[1] = (byte) this.shotAngle;

        bytes[2] = (byte) (this.previousGameLayoutNr >> 8);
        bytes[3] = (byte) (this.previousGameLayoutNr & 0x0f);

        return bytes;
    }
}
