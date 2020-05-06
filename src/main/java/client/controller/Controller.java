package client.controller;

import client.interfaces.ConfigurationWindowListener;
import client.interfaces.GameWindowListener;
import client.model.network.GameConfiguration;
import client.model.network.packets.gameLayout.GameLayout;
import client.model.network.TcpHandler;
import client.view.ConfigurationWindow;
import client.view.GameWindow;

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
        //update game layout in the game window
    }

    @Override
    public void configurationWindowChanged(GameConfiguration gameConfiguration, Object source) {
        this.configurationWindow.dispose();
        this.gameWindow.setVisible(true);
        this.tcpHandler.start(gameConfiguration, this);
    }

    @Override
    public void gameWindowChanged(Object source) {
        //catch keyboard pressing and detect direction of moving and angle of shoot
        //build object of class GamerMoving and call toBytes() on it

        //send packet to server by UDP
        byte[] bytes = "5".getBytes();
        DatagramPacket datagramPacket = new DatagramPacket(bytes, bytes.length, this.udpHost);
        try {
            this.udpSocket.send(datagramPacket);
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("Nieudane wysłanie pakietu z ruchem i strzałem gracza po UDP");
        }
    }
}