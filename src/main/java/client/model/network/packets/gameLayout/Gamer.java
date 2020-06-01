package client.model.network.packets.gameLayout;

public class Gamer {

    private int id;
    private int points;
    private double x;
    private double y;

    public Gamer(int id, int points, double x, double y) {
        this.id = id;
        this.points = points;
        this.x = x;
        this.y = y;
    }

    public int getId() {
        return id;
    }

    public int getPoints() {
        return points;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
