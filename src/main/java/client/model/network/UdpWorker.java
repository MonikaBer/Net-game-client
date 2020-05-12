package client.model.network;

import client.controller.Controller;
import client.model.exceptions.ParseGameLayoutException;
import client.model.network.packets.gameLayout.GameLayout;

import javax.swing.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.List;

import static client.model.helpers.Helper.convertGamerId;
import static client.model.helpers.Helper.convertToString;
import static client.model.network.packets.gameLayout.GameLayout.checkIfGameLayout;
import static client.model.network.packets.gameLayout.GameLayout.parseToGameLayout;
import java.nio.ByteBuffer;

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
                if (!checkIfGameLayout(buffer) )
                    continue;

                GameLayout gameLayout = parseToGameLayout(buffer);
                System.out.println("stan gry -> " + gameLayout.toString());
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

//int moving = convertToInt(buffer, udpPacket.getLength());
//int moving = (char)buffer[0] - '0';
//                moving = moving << 8;
//                moving += second - '0';
//                moving = moving << 8;
//                moving += third - '0';
//                moving = moving << 8;
//                moving += fourth - '0';
