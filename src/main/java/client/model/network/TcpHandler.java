package client.model.network;

import client.controller.Controller;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.TimeUnit;

import static client.model.helpers.Helper.convertGamerId;
import static client.model.helpers.Helper.convertToString;

public class TcpHandler {

    private Thread tcpHandler;
    private Thread healthCheck;

    public TcpHandler() {}

    public void start(GameConfiguration gameConfiguration, Controller controller) {

        Runnable runnable = () -> {
            Thread healthCheck = null;
            int len;
            String receivedPacket;

            while (true) {
                final Socket tcpSocket;
                final DatagramSocket udpClientSocket;

                if (healthCheck != null) {
                    healthCheck.interrupt();
                    healthCheck = null;
                }

                //try to connect with server by TCP
                try {
                    System.out.println("Próbuję połączyć się z serwerem po TCP...");
                    tcpSocket = new Socket(gameConfiguration.getTcpHost().getHostName(), gameConfiguration.getTcpHost().getPort());
                    System.out.println("Udało się połączyć z serwerem po TCP");
                } catch (IOException e) {
                    //e.printStackTrace();
                    System.out.println("Nieudana próba połączenia z serwerem po TCP");
                    continue;
                }

                //wait for your gamer id on TCP
                String gamerId;
                byte[] packetWithGamerId = new byte[6];
                try {
                    System.out.println("Czekam na pakiet od serwera z moim id gracza po TCP");
                    len = tcpSocket.getInputStream().read(packetWithGamerId);
                    if (len != 6) {
                        System.out.println("Serwer wysłał niespodziewany pakiet, miał być pakiet z 5-znakowym id gracza <XID>");
                        continue;
                    }
                    gamerId = convertGamerId(packetWithGamerId, 1, 5);
                    System.out.println("Odebrano id gracza równe: " + gamerId);
                } catch (IOException e) {
                    //e.printStackTrace();
                    System.out.println("Błąd połączenia TCP przy oczekiwaniu na pakiet z unikalnym id gracza");
                    continue;
                }

                //lambda function for healthCheck thread
                //healthCheck sends to server packets "alive"
                Runnable runnable1 = () -> {
                    while (true) {
                        try {
                            TimeUnit.SECONDS.sleep(1);
                        } catch (InterruptedException e) {
                            //e.printStackTrace();
                            System.out.println("Problem z uśpieniem wątku TCP wysyłającego pakiety aktywności");
                            continue;
                        }
                        synchronized (tcpSocket) {
                            try {
                                tcpSocket.getOutputStream().write("alive".getBytes());
                            } catch (IOException e) {
                                //e.printStackTrace();
                                System.out.println("Nieudane wysłanie po TCP pakietu aktywności");
                                break;
                            }
                        }
                    }
                };
                healthCheck = new Thread(runnable1);
                healthCheck.start();                    //start sending packets "alive"

                //TODO: keys exchange -> now communication without cryptography

                try {
                    udpClientSocket = new DatagramSocket();
                } catch (SocketException e) {
                    //e.printStackTrace();
                    System.out.println("Nieudana próba utworzenia gniazda UDP");
                    continue;
                }

                controller.setUdpSocket(udpClientSocket);
                controller.setUdpHost(gameConfiguration.getUdpHost());

                //create UDP started packet
                DatagramPacket startUdpPacket = new DatagramPacket(packetWithGamerId, packetWithGamerId.length,
                  gameConfiguration.getUdpHost());

                try {
                    tcpSocket.setSoTimeout(1000);
                    System.out.println("Przesyłam pakiet startowy po UDP do serwera");
                    byte[] startPacket = new byte[5];
                    while (true) {
                        udpClientSocket.send(startUdpPacket);
                        try {
                            len = tcpSocket.getInputStream().read(startPacket);
                            //TODO: ->  if (len != 5)
                            receivedPacket = convertToString(startPacket);
                            if (receivedPacket.equals("start")) {
                                System.out.println("Udane zainicjowanie UDP, otrzymałem pakiet 'start' po TCP od serwera");
                                break;
                            } else {
                                //TODO: handle this situation
                                System.out.println("Serwer wysłał niespodziewany pakiet zwrotny zamiast pakietu 'start'");
                            }
                        } catch (SocketTimeoutException e) {
                            System.out.println("Problem z połączeniem TCP podczas inicjalizacji UDP -> jeszcze nieobsłużone");
                            //TODO: handle this situation -> logger info wait for a second
                        }
                    }
                } catch (IOException e) {
                    //e.printStackTrace();
                    System.out.println("Problem z wysłaniem pakietu startowego po UDP (lub ustawieniem timeout na TCP na 1 sek)");
                    continue;
                }

                try {
                    tcpSocket.setSoTimeout(0);
                } catch (SocketException e) {
                    //e.printStackTrace();
                    System.out.println("Problem z ustawieniem domyślnego timeout na TCP");
                    continue;
                }

                try (UdpWorker udpWorker = new UdpWorker(controller)) {
                    byte[] stopPacket = new byte[4];
                    byte[] startPacket = new byte[5];
                    while (true) {
                        udpWorker.execute();                //start of game
                        while (true) {
                            len = tcpSocket.getInputStream().read(stopPacket);               //wait for stop game
                            receivedPacket = convertToString(stopPacket);
                            if (len != 4 || !receivedPacket.equals("stop")) {
                                //System.out.println("Niespodziewany pakiet po TCP zamiast pakietu 'stop'");
                                continue;
                            }
                            System.out.println("Otrzymałem pakiet 'stop', koniec rozgrywki");
                            break;
                        }

                        udpWorker.close();

                        //TODO: add event for GUI-> end of game
                        //java.awt.Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(null);

                        System.out.println("Teraz zawieszam się w oczekiwaniu na start");
                        while (true) {
                            len = tcpSocket.getInputStream().read(startPacket);               //wait for start of game
                            receivedPacket = convertToString(startPacket);
                            if (len != 5 || !receivedPacket.equals("start")) {
                                //System.out.println("Niespodziewany pakiet zamiast pakietu 'start'");
                                continue;
                            }
                            System.out.println("Otrzymałem pakiet 'start' od serwera, wznawiam grę");
                            break;
                        }
                    }
                } catch (IOException ex) {
                    //ex.printStackTrace();
                    System.out.println("Problem z połączeniem TCP");
                    System.out.println("Kończę rozgrywkę");
                    continue;
                }
            }
        };

        tcpHandler = new Thread(runnable);
        tcpHandler.start();                     //start thread responsible for handling TCP connection
    }
}
