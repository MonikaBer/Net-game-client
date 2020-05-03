package client.view;

import client.interfaces.GameWindowListener;

import javax.swing.*;

public class GameWindow extends JFrame {

    private GameWindowListener gameWindowListener;

    public GameWindow() {

        this.setSize(800, 600);
        this.setResizable(false);
        this.setTitle("Gra");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(null);
        this.setVisible(false);
    }

    public void addListener(GameWindowListener gameWindowListener) {
        this.gameWindowListener = gameWindowListener;
    }
}
