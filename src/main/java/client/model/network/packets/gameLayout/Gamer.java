package client.model.network.packets.gameLayout;

public class Gamer {

    private int gamerId;
//    private boolean active;
//    private int points;
    private double x;
    private double y;

    public Gamer(int gamerId, double x, double y) {
        this.gamerId = gamerId;
        this.x = x;
        this.y = y;
    }

//    public Gamer(int gamerId, boolean active, int points, int x, int y) {
//        this.gamerId = gamerId;
//        this.active = active;
//        this.points = points;
//        this.x = x;
//        this.y = y;
//    }


    public int getGamerId() {
        return gamerId;
    }

    public void setGamerId(int gamerId) {
        this.gamerId = gamerId;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }
}
