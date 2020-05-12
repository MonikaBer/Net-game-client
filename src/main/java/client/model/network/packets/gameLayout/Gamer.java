package client.model.network.packets.gameLayout;

public class Gamer {

    private int gamerId;
    private double x;
    private double y;

    public Gamer(int gamerId, double x, double y) {
        this.gamerId = gamerId;
        this.x = x;
        this.y = y;
    }

    public int getGamerId() {
        return gamerId;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
