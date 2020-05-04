package client.model.network;

import client.controller.Controller;
import client.model.network.packets.gameLayout.Bullet;
import client.model.network.packets.gameLayout.GameLayout;
import client.model.network.packets.gameLayout.Gamer;
import client.model.exceptions.ParseGameLayoutException;

import javax.swing.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.List;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import static client.model.network.packets.gameLayout.GameLayout.parseToGameLayout;

public class UdpWorker extends SwingWorker<Object, GameLayout> implements AutoCloseable {

    private Controller controller;
    private DatagramSocket udpSocket;

    public UdpWorker(Controller controller) {
        super();
        this.controller = controller;
        this.udpSocket = this.controller.getUdpSocket();
    }

    @Override
    protected Object doInBackground() throws Exception {
        byte[] buffer = new byte[1024];
        DatagramPacket udpPacket = new DatagramPacket(buffer, buffer.length);

        while (true) {
            this.udpSocket.receive(udpPacket);

            try {
                publish(parseToGameLayout(buffer));
            } catch (ParseGameLayoutException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    protected void process(List<GameLayout> gameLayouts) {
        if (!gameLayouts.isEmpty()) {
            controller.updateGameLayout(gameLayouts.get(gameLayouts.size() - 1));
        }
    }

    @Override
    public void close() {
        cancel(true);
    }
}
