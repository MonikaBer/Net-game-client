package client.view.gameWindow;

import client.interfaces.GameWindowListener;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class GameWindow extends JFrame implements KeyListener {

    private GamePanel gamePanel;
    private GameWindowListener gameWindowListener;

    public GameWindow() {
        this.createGameWindow();
        this.setGameWindowProperties();
    }

    public void createGameWindow() {
        this.gamePanel = new GamePanel();
        this.setContentPane(this.gamePanel);

        this.addKeyListener(this);
        this.pack();
    }

    public void setGameWindowProperties() {
        this.setSize(600, 600);
        this.setResizable(false);
        this.setTitle("Gra");
        this.setLayout(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(false);
    }

    public GamePanel getGamePanel() {
        return gamePanel;
    }

    public void addListener(GameWindowListener gameWindowListener) {
        this.gameWindowListener = gameWindowListener;
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        gameWindowListener.gameWindowChanged(keyEvent);
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {
    }
}