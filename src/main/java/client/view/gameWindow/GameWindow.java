package client.view.gameWindow;

import client.interfaces.GameWindowListener;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class GameWindow extends JFrame implements KeyListener, MouseListener {

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
        this.addMouseListener(this);
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
        gameWindowListener.gameWindowChanged(keyEvent, keyEvent.getKeyChar(), 0, 0);
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {}

    @Override
    public void keyReleased(KeyEvent keyEvent) {}

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {
        gameWindowListener.gameWindowChanged(mouseEvent, 'm', mouseEvent.getX(), mouseEvent.getY());
    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {

    }
}
