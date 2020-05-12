package client.model.network.packets.gameLayout;

import client.model.exceptions.ParseGameLayoutException;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

public class GameLayout {

    //private int gameId;
    private int packetId;
    private ArrayList<Gamer> gamers;
    //private ArrayList<Bullet> bullets;

//    public GameLayout(int gameId, int packetId, List<Gamer> gamers, List<Bullet> bullets) {
//        this.gameId = gameId;
//        this.packetId = packetId;
//        this.gamers = gamers;
//        this.bullets = bullets;
//    }

    public GameLayout(int packetId, ArrayList<Gamer> gamers) {
        this.packetId = packetId;
        this.gamers = gamers;
    }

    public static boolean checkIfGameLayout(byte[] buffer) {

        char tag = (char) buffer[0];
        if (tag != 'Y')
            return false;
        return true;
    }

    public static GameLayout parseToGameLayout(byte[] buffer) throws ParseGameLayoutException {
        if (buffer.length < 3)
            throw new ParseGameLayoutException();

        int packetId = Byte.toUnsignedInt(buffer[1]);
        packetId = packetId * 256 + Byte.toUnsignedInt(buffer[2]);

        ArrayList<Gamer> gamers = new ArrayList<Gamer>();
        Gamer gamer;
        int gamerId;
        int xTemp, yTemp;
        double x, y;
        int i = 3;
        while (buffer[i] != 0 && i + 4 < buffer.length) {
            gamerId = buffer[i];
            i++;

            xTemp = Byte.toUnsignedInt(buffer[i]) * 256;
            i++;
            xTemp += Byte.toUnsignedInt(buffer[i]);
            i++;
            x = (double) xTemp / 100.0;

            yTemp = Byte.toUnsignedInt(buffer[i]) * 256;
            i++;
            yTemp += Byte.toUnsignedInt(buffer[i]);
            i++;
            y = (double) yTemp / 100.0;

            gamer = new Gamer(gamerId, x, y);
            gamers.add(gamer);
        }

        GameLayout gameLayout = new GameLayout(packetId, gamers);
        return gameLayout;
    }

    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append(this.packetId);
        str.append(": ");
        for (int i = 0; i < this.gamers.size(); i++) {
            str.append(this.gamers.get(i).getGamerId());
            str.append(",");
            str.append(this.gamers.get(i).getX());
            str.append(",");
            str.append(this.gamers.get(i).getY());
            str.append("; ");
        }
        return str.toString();
    }

//    public static GameLayout parseToGameLayout(byte[] buffer) throws ParseGameLayoutException {
//        if (buffer.length < 7)
//            throw new ParseGameLayoutException();
//
//        int gameId = buffer[0];
//        int packetId = buffer[1];
//        List<Gamer> gamers = null;
//        List<Bullet> bullets = null;
//        int crc;
//
//        int i = buffer.length - 4;
//        crc = buffer[i]; i++;
//        crc = crc << 8;
//        crc += buffer[i]; i++;
//        crc = crc << 8;
//        crc += buffer[i]; i++;
//        crc = crc << 8;
//        crc += buffer[i];
//
//        //count crc
//        Checksum checksum = new CRC32();
//        checksum.update(buffer, 0, buffer.length-4);
//        int checksumValue = (int)checksum.getValue();
//
//        if (crc != checksumValue)
//            throw new ParseGameLayoutException();
//
//        Gamer gamer;
//        int gamerId;
//        boolean active;
//        int points, x, y;
//        i = 2;
//        while (buffer[i] != 255) {
//            if (i+5 < buffer.length)
//                throw new ParseGameLayoutException();
//
//            gamerId = buffer[i]; i++;
//            int gamerState = buffer[i]; i++;
//            gamerState = gamerState >> 7;
//            if (gamerState == 1)  active = false;   //ustalić że jak 1 to inactive
//            else  active = true;
//            if (active)  gamerState -= 128;
//            points = gamerState;
//            x = buffer[i]; i++;                     //ustalić że najpierw starszy bajt, potem młodszy bajt
//            x = x << 8;
//            x += buffer[i]; i++;
//            y = buffer[i]; i++;
//            y = y << 8;
//            y += buffer[i]; i++;
//
//            gamer = new Gamer(gamerId, active, points, x, y);
//            gamers.add(gamer);
//        }
//
//        i++;  //avoid special byte '255'
//
//        Bullet bullet;
//
//        while (i+8 < buffer.length ) {
//            x = buffer[i]; i++;
//            x = x << 8;
//            x += buffer[i]; i++;
//            y = buffer[i]; i++;
//            y = y << 8;
//            y += buffer[i]; i++;
//
//            bullet = new Bullet(x, y);
//            bullets.add(bullet);
//        }
//
//        GameLayout gameLayout = new GameLayout(gameId, packetId, gamers, bullets);
//        return gameLayout;
//    }

//    public int getGameId() {
//        return gameId;
//    }

//    public void setGameId(int gameId) {
//        this.gameId = gameId;
//    }

    public int getPacketId() {
        return packetId;
    }

    public void setPacketId(int packetId) {
        this.packetId = packetId;
    }

    public ArrayList<Gamer> getGamers() {
        return gamers;
    }

    public void setGamers(ArrayList<Gamer> gamers) {
        this.gamers = gamers;
    }
}

//    public List<Bullet> getBullets() {
//        return bullets;
//    }
//
//    public void setBullets(List<Bullet> bullets) {
//        this.bullets = bullets;
//    }
