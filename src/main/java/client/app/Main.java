package client.app;

import client.controller.Controller;
import client.interfaces.ConfigurationWindowListener;
import client.model.Model;
import client.network.TcpHandler;
import client.network.UdpWorker;
import client.view.ConfigurationWindow;
import client.view.GameWindow;

import javax.swing.*;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Main {

    public static void main(String[] args) {

        Runnable program = new Runnable() {
            @Override
            public void run() {
                ConfigurationWindow configurationWindow = new ConfigurationWindow();
                GameWindow gameWindow = new GameWindow();
                TcpHandler tcpHandler = new TcpHandler();
                Controller controller = new Controller(gameWindow, configurationWindow, tcpHandler);
                configurationWindow.addListener(controller);
                gameWindow.addListener(controller);
            }
        };
        SwingUtilities.invokeLater(program);

    }
}