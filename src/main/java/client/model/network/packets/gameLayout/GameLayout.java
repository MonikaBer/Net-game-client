package client.model.network.packets.gameLayout;

import client.model.exceptions.ParseGameLayoutException;

import java.util.List;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

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

    public static GameLayout parseToGameLayout(byte[] buffer) throws ParseGameLayoutException {
        if (buffer.length < 7)
            throw new ParseGameLayoutException();

        int gameId = buffer[0];
        int packetId = buffer[1];
        List<Gamer> gamers = null;
        List<Bullet> bullets = null;
        int crc;

        int i = buffer.length - 4;
        crc = buffer[i]; i++;
        crc = crc << 8;
        crc += buffer[i]; i++;
        crc = crc << 8;
        crc += buffer[i]; i++;
        crc = crc << 8;
        crc += buffer[i];

        //count crc
        Checksum checksum = new CRC32();
        checksum.update(buffer, 0, buffer.length-4);
        int checksumValue = (int)checksum.getValue();

        if (crc != checksumValue)
            throw new ParseGameLayoutException();

        Gamer gamer;
        int gamerId;
        boolean active;
        int points, x, y;
        i = 2;
        while (buffer[i] != 255) {
            if (i+5 < buffer.length)
                throw new ParseGameLayoutException();

            gamerId = buffer[i]; i++;
            int gamerState = buffer[i]; i++;
            gamerState = gamerState >> 7;
            if (gamerState == 1)  active = false;   //ustalić że jak 1 to inactive
            else  active = true;
            if (active)  gamerState -= 128;
            points = gamerState;
            x = buffer[i]; i++;                     //ustalić że najpierw starszy bajt, potem młodszy bajt
            x = x << 8;
            x += buffer[i]; i++;
            y = buffer[i]; i++;
            y = y << 8;
            y += buffer[i]; i++;

            gamer = new Gamer(gamerId, active, points, x, y);
            gamers.add(gamer);
        }

        i++;  //avoid special byte '255'

        Bullet bullet;

        while (i+8 < buffer.length ) {
            x = buffer[i]; i++;
            x = x << 8;
            x += buffer[i]; i++;
            y = buffer[i]; i++;
            y = y << 8;
            y += buffer[i]; i++;

            bullet = new Bullet(x, y);
            bullets.add(bullet);
        }

        GameLayout gameLayout = new GameLayout(gameId, packetId, gamers, bullets);
        return gameLayout;
    }
}
