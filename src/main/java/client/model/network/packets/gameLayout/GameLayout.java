package client.model.network.packets.gameLayout;

import client.model.exceptions.ParseGameLayoutException;
import client.model.helpers.Helper;

import java.util.zip.CRC32;
import java.util.zip.Checksum;

import java.util.ArrayList;

public class GameLayout {

    private int packetId;
    private ArrayList<Gamer> gamers;
    private ArrayList<Bullet> bullets;

    public GameLayout(int packetId, ArrayList<Gamer> gamers, ArrayList<Bullet> bullets) {
        this.packetId = packetId;
        this.gamers = gamers;
        this.bullets = bullets;
    }

    public static boolean ifGameLayout(byte[] buffer) {
        char tag = (char)buffer[0];
        if (tag != 'Y')
            return false;
        return true;
    }

    public static GameLayout parseToGameLayout(byte[] buffer, int packetLength) throws ParseGameLayoutException {
        if (packetLength < 7)
            throw new ParseGameLayoutException();

        int crcSum = Helper.getNumberFromBuffer(buffer, packetLength-4, packetLength-1);
        Checksum checksum = new CRC32();
        checksum.update(buffer, 0, packetLength-4);
        int checksumValue = (int)checksum.getValue();
        if (checksumValue != crcSum) {
            throw new ParseGameLayoutException();
        }

        int packetId = Byte.toUnsignedInt(buffer[1]);
        packetId = packetId * 256 + Byte.toUnsignedInt(buffer[2]);

        ArrayList<Gamer> gamers = new ArrayList<>();
        int gamerId;
        int gamerPoints;
        double x, y;
        int i = 3;
        while (Byte.toUnsignedInt(buffer[i]) != 0) {
            gamerId = Byte.toUnsignedInt(buffer[i]);
            i++;
            gamerPoints = Byte.toUnsignedInt(buffer[i]);
            i++;
            x = getDoubleFromBuffer(buffer, i);
            i += 2;
            y = getDoubleFromBuffer(buffer, i);
            i += 2;
            gamers.add(new Gamer(gamerId, gamerPoints, x, y));
        }

        ArrayList<Bullet> bullets = new ArrayList<>();
        i++;
        while (i + 7 < packetLength) {
            x = getDoubleFromBuffer(buffer, i);
            i += 2;
            y = getDoubleFromBuffer(buffer, i);
            i += 2;
            bullets.add(new Bullet(x, y));
        }

        return new GameLayout(packetId, gamers, bullets);
    }

    public static double getDoubleFromBuffer(byte[] buffer, int index) {
        int i = index;
        int number = Byte.toUnsignedInt(buffer[i]) * 256;
        number += Byte.toUnsignedInt(buffer[++i]);
        return (double) number / 100.0;
    }

    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("Pakiet: ");
        str.append(this.packetId);
        str.append(" -> Gracze: ");
        for (int i = 0; i < this.gamers.size(); i++) {
            str.append("(");
            str.append(this.gamers.get(i).getId());
            str.append(",");
            str.append(this.gamers.get(i).getPoints());
            str.append(",");
            str.append(this.gamers.get(i).getX());
            str.append(",");
            str.append(this.gamers.get(i).getY());
            str.append("), ");
        }
        str.append(" Kule: ");
        for (int i = 0; i < this.bullets.size(); i++) {
            str.append("(");
            str.append(this.bullets.get(i).getX());
            str.append(",");
            str.append(this.bullets.get(i).getY());
            str.append("), ");
        }
        return str.toString();
    }

    public int getPacketId() {
        return packetId;
    }

    public ArrayList<Gamer> getGamers() {
        return gamers;
    }

    public ArrayList<Bullet> getBullets() {
        return bullets;
    }
}

