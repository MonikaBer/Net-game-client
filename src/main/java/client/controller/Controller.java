package client.controller;

import client.interfaces.ConfigurationWindowListener;
import client.interfaces.GameWindowListener;
import client.model.network.GameConfiguration;
import client.model.gameLayout.GameLayout;
import client.model.network.TcpHandler;
import client.view.ConfigurationWindow;
import client.view.GameWindow;

import java.net.DatagramSocket;


public class Controller implements ConfigurationWindowListener, GameWindowListener {

    private GameWindow gameWindow;
    private ConfigurationWindow configurationWindow;
    private TcpHandler tcpHandler;
    private DatagramSocket udpSocket;

    public Controller(GameWindow gameWindow, ConfigurationWindow configurationWindow, TcpHandler tcpHandler) {
        this.gameWindow = gameWindow;
        this.configurationWindow = configurationWindow;
        this.tcpHandler = tcpHandler;
    }

    public void setUdpSocket(DatagramSocket udpSocket) {
        this.udpSocket = udpSocket;
    }

    public DatagramSocket getUdpSocket() {
        return this.udpSocket;
    }

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

    }
}