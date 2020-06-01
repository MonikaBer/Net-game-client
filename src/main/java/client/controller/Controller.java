package client.controller;

import client.interfaces.ConfigurationWindowListener;
import client.interfaces.GameWindowListener;
import client.model.network.GameConfiguration;
import client.model.network.packets.gameLayout.GameLayout;
import client.model.network.TcpHandler;
import client.model.network.packets.gamerAction.GamerMoving;
import client.model.network.packets.gamerAction.GamerShot;
import client.view.ConfigurationWindow;
import client.view.gameWindow.GameWindow;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;


public class Controller implements ConfigurationWindowListener, GameWindowListener {

    private GameWindow gameWindow;
    private ConfigurationWindow configurationWindow;
    private TcpHandler tcpHandler;
    private DatagramSocket udpSocket;
    private InetSocketAddress udpHost;
    private int previousGameLayoutNr = -1;

    public Controller(GameWindow gameWindow, ConfigurationWindow configurationWindow, TcpHandler tcpHandler) {
        this.gameWindow = gameWindow;
        this.configurationWindow = configurationWindow;
        this.tcpHandler = tcpHandler;
    }

    public DatagramSocket getUdpSocket() {
        return this.udpSocket;
    }

    public void setUdpSocket(DatagramSocket udpSocket) {
        this.udpSocket = udpSocket;
    }

    public void setUdpHost(InetSocketAddress udpHost) { this.udpHost = udpHost; }

    public void updateGameLayout(GameLayout gameLayout) {
        this.previousGameLayoutNr = gameLayout.getPacketId();
        for (int i = 0; i < gameLayout.getGamers().size(); i++) {
            int gamerId = gameLayout.getGamers().get(i).getId();
            int gamerPoints = gameLayout.getGamers().get(i).getPoints();
            double x = gameLayout.getGamers().get(i).getX();
            double y = gameLayout.getGamers().get(i).getY();
            this.gameWindow.getGamePanel().setGamerPosition(gamerId, x, y);
        }
        this.gameWindow.getGamePanel().setBullets(gameLayout.getBullets());
        this.gameWindow.setContentPane(this.gameWindow.getGamePanel());
    }

    @Override
    public void configurationWindowChanged(GameConfiguration gameConfiguration, Object source) {
        this.configurationWindow.dispose();
        this.gameWindow.setVisible(true);
        this.tcpHandler.start(gameConfiguration, this);
    }

    @Override
    public void gameWindowChanged(Object source, char key, int cursorX, int cursorY) {
        //catch pressed key or clicked mouse, build object of class GamerMoving or GamerShot and call toBytes() on it
        DatagramPacket datagramPacket;
        if (cursorX != 0 || cursorY != 0) {     //mouse clicked
            int shotAngle = Math.round((float)Math.atan(Math.tan((double)cursorY / (double)cursorX)));
            GamerShot gamerShot = new GamerShot(shotAngle, this.previousGameLayoutNr);
            byte[] udpShotPacket = gamerShot.toBytes();
            datagramPacket = new DatagramPacket(udpShotPacket, udpShotPacket.length, this.udpHost);
        }
        else {       //key pressed
            int movingDirection;
            switch(key) {
                case 's': movingDirection = 0; break;
                case 'w': movingDirection = 1; break;
                case 'e': movingDirection = 2; break;
                case 'd': movingDirection = 3; break;
                case 'c': movingDirection = 4; break;
                case 'x': movingDirection = 5; break;
                case 'z': movingDirection = 6; break;
                case 'a': movingDirection = 7; break;
                case 'q': movingDirection = 8; break;
                default: return;
            }
            GamerMoving gamerMoving = new GamerMoving(movingDirection, this.previousGameLayoutNr);
            byte[] udpMovingPacket = gamerMoving.toBytes();
            datagramPacket = new DatagramPacket(udpMovingPacket, udpMovingPacket.length, this.udpHost);
        }

        //send packet UDP to server by UDP
        try {
            this.udpSocket.send(datagramPacket);
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("Nieudane wysłanie pakietu z ruchem gracza po UDP");
        }
    }
}