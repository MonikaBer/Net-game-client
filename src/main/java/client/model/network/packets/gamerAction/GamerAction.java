package client.model.network.packets.gamerAction;

public class GamerAction {

    protected int previousGameLayoutNr;

    public GamerAction(int previousGameLayoutNr) {
        this.previousGameLayoutNr = previousGameLayoutNr;
    }

    public int getPreviousGameLayoutNr() {
        return previousGameLayoutNr;
    }
}
