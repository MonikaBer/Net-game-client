package client.model.network;

import client.controller.Controller;
import client.model.network.packets.gameLayout.GameLayout;

import javax.swing.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.List;


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
            if (this.isCancelled()) {
                return false;
            }
            this.udpSocket.receive(udpPacket);

            //try {
                //publish(parseToGameLayout(buffer));
                StringBuilder sBuilder = new StringBuilder();
                for(int i = 0; i < buffer.length; i++) {
                    sBuilder.append((char)buffer[i]);
                }
                String bufferInString = sBuilder.toString();
                System.out.println(bufferInString);
            //} catch (ParseGameLayoutException ex) {
            //    ex.printStackTrace();
            //}
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
