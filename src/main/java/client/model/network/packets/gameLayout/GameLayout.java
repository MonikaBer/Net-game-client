package client.model.network.packets.gameLayout;

import client.model.exceptions.ParseGameLayoutException;

import java.util.ArrayList;

public class GameLayout {

    private int packetId;
    private ArrayList<Gamer> gamers;

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

    public int getPacketId() {
        return packetId;
    }

    public ArrayList<Gamer> getGamers() {
        return gamers;
    }

    public void setGamers(ArrayList<Gamer> gamers) {
        this.gamers = gamers;
    }
}

