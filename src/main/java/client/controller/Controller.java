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
    public void gameWindowChanged(KeyEvent source) {
        //catch pressed key or clicked mouse, build object of class GamerMoving or GamerShot and call toBytes() on it
        DatagramPacket datagramPacket = null;
            int movingDirection = -1;
            int shotAngle = -1;
            switch(source.getKeyCode()) {
                case KeyEvent.VK_S: movingDirection = 0; break;
                case KeyEvent.VK_W: movingDirection = 1; break;
                case KeyEvent.VK_E: movingDirection = 2; break;
                case KeyEvent.VK_D: movingDirection = 3; break;
                case KeyEvent.VK_C: movingDirection = 4; break;
                case KeyEvent.VK_X: movingDirection = 5; break;
                case KeyEvent.VK_Z: movingDirection = 6; break;
                case KeyEvent.VK_A: movingDirection = 7; break;
                case KeyEvent.VK_Q: movingDirection = 8; break;
                case KeyEvent.VK_DOWN:  shotAngle = (int)Math.round(90*254/359.0); break;   //down arrow
                case KeyEvent.VK_LEFT:  shotAngle = (int)Math.round(180*254/359.0); break;  //left arrow
                case KeyEvent.VK_UP:    shotAngle = (int)Math.round(270*254/359.0); break;  //up arrow
                case KeyEvent.VK_RIGHT: shotAngle = (int)Math.round(0*254/359.0); break;    //right arrow
                default: return;
            }
            if (shotAngle != -1) {  //shot
                GamerShot gamerShot = new GamerShot(shotAngle, this.previousGameLayoutNr);
                byte[] udpShotPacket = gamerShot.toBytes();
                datagramPacket = new DatagramPacket(udpShotPacket, udpShotPacket.length, this.udpHost);
            } else if (movingDirection != -1) {    //motion
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