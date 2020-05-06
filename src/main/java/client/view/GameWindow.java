package client.view;

import client.interfaces.GameWindowListener;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class GameWindow extends JFrame implements KeyListener {

    private GameWindowListener gameWindowListener;

    public GameWindow() {

        this.setSize(800, 600);
        this.setResizable(false);
        this.setTitle("Gra");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(null);
        this.setVisible(false);
        addKeyListener(this);
    }

    public void addListener(GameWindowListener gameWindowListener) {
        this.gameWindowListener = gameWindowListener;
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        gameWindowListener.gameWindowChanged(keyEvent);
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {}

    @Override
    public void keyReleased(KeyEvent keyEvent) {}
}
