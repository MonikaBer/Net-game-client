package client.model;

public class Gamer {

    private int gamerId;
    private boolean active;
    private int points;
    private int x;
    private int y;

    public Gamer(int gamerId, boolean active, int points, int x, int y) {
        this.gamerId = gamerId;
        this.active = active;
        this.points = points;
        this.x = x;
        this.y = y;
    }
}
