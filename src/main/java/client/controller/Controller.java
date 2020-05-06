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
        //update game layout in game window
    }

    @Override
    public void configurationWindowChanged(GameConfiguration gameConfiguration, Object source) {
        this.configurationWindow.dispose();
        this.gameWindow.setVisible(true);
        this.tcpHandler.start(gameConfiguration, this);
    }

    @Override
    public void gameWindowChanged(Object source) {
        //przechwycenie klawiszy i odczytanie kierunku ruchu i kątu strzału gracza
        //zbudowanie obiektu GamerMoving i wywołanie toBytes() na nim

        //wysłanie pakietu po udp do serwera
        String str = "5";
        byte[] bytes = str.getBytes();
        DatagramPacket datagramPacket = new DatagramPacket(bytes, bytes.length, this.udpHost);
        try {
            this.udpSocket.send(datagramPacket);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}