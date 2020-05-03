package client.model.gameLayout;

import java.util.List;

public class GameLayout {

    private int gameId;
    private int packetId;
    private List<Gamer> gamers;
    private List<Bullet> bullets;

    public GameLayout(int gameId, int packetId, List<Gamer> gamers, List<Bullet> bullets) {
        this.gameId = gameId;
        this.packetId = packetId;
        this.gamers = gamers;
        this.bullets = bullets;
    }
}
