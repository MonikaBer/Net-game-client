package client.app;

import client.controller.Controller;
import client.model.network.TcpHandler;
import client.view.ConfigurationWindow;
import client.view.gameWindow.GameWindow;

import javax.swing.*;

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