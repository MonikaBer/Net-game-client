package client.model.network;

import client.controller.Controller;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.TimeUnit;

public class TcpHandler {

    private Thread tcpHandler;
    private Thread healthCheck;


    public TcpHandler() {}

    public void start(GameConfiguration gameConfiguration, Controller controller) {

        Runnable runnable = () -> {
            Thread healthCheck = null;

            while (true) {
                final Socket tcpSocket;
                final DatagramSocket udpClientSocket;
                if (healthCheck != null) {
                    healthCheck.interrupt();
                    healthCheck = null;
                }

                try {
                    tcpSocket = new Socket(gameConfiguration.getTcpHost().getHostName(), gameConfiguration.getTcpHost().getPort());
                } catch (IOException e) {
                    e.printStackTrace();
                    continue;
                }

                Runnable runnable1 = () -> {
                    while (true) {
                        try {
                            TimeUnit.SECONDS.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            continue;
                        }
                        synchronized (tcpSocket) {
                            try {
                                tcpSocket.getOutputStream().write("I am alive".getBytes());
                            } catch (IOException e) {
                                e.printStackTrace();
                                continue;
                            }
                        }
                    }
                };

                healthCheck = new Thread(runnable1);
                healthCheck.start();

                //TODO: negocjacja kluczy

                try {
                    udpClientSocket = new DatagramSocket();
                } catch (SocketException e) {
                    e.printStackTrace();
                    continue;
                }

                controller.setUdpSocket(udpClientSocket);

                byte[] startUdpMsg = "UDP start".getBytes();
                DatagramPacket startUdpPacket = new DatagramPacket(startUdpMsg, startUdpMsg.length, gameConfiguration.getUdpHost());
                try {
                    tcpSocket.setSoTimeout(1000);
                    while (true) {
                        udpClientSocket.send(startUdpPacket);
                        try {
                            tcpSocket.getInputStream().read();
                            break;
                        } catch (SocketTimeoutException e) {
                            //TODO: logger info czekanie przez sekundę
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    continue;
                }

                try (UdpWorker udpWorker = new UdpWorker(controller)) {
                    while (true) {
                        tcpSocket.getInputStream().read();    //wait for start of game

                        //start of game
                        udpWorker.execute();
                        tcpSocket.getInputStream().read();

                        udpWorker.cancel(true);

                        //TODO: dodać event że się skończyła gra (to ma pójść do gui)
                        java.awt.Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(null);
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                    continue;
                }
            }
        };

        tcpHandler = new Thread(runnable);
        tcpHandler.start();
    }
}
