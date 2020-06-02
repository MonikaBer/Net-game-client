package client.model.network;

import client.controller.Controller;
import client.model.exceptions.ParseGameLayoutException;
import client.model.network.packets.gameLayout.GameLayout;

import javax.swing.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.List;

import static client.model.network.packets.gameLayout.GameLayout.ifGameLayout;
import static client.model.network.packets.gameLayout.GameLayout.parseToGameLayout;


public class UdpWorker extends SwingWorker<Boolean, GameLayout> implements AutoCloseable {
    private Controller controller;
    private DatagramSocket udpSocket;

    public UdpWorker(Controller controller) {
        super();
        this.controller = controller;
        this.udpSocket = this.controller.getUdpSocket();
    }

    @Override
    protected Boolean doInBackground() throws Exception {

        byte[] buffer = new byte[1024];
        DatagramPacket udpPacket = new DatagramPacket(buffer, buffer.length);

        while (true) {
            if (this.isCancelled()) return false;
            this.udpSocket.receive(udpPacket);
            try {
                if (!ifGameLayout(buffer) )
                    continue;

                GameLayout gameLayout = parseToGameLayout(buffer, udpPacket.getLength());
                System.out.println("STAN GRY -> " + gameLayout.toString());
                publish(gameLayout);

            } catch (ParseGameLayoutException ex) {
                ex.printStackTrace();
                System.out.println("Nieudane parsowanie pakietu ze stanem gry");
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