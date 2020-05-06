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
            boolean isServerDisconnected = false;
            boolean isSuccess = false;

            System.out.println("Próbuję połączyć się z serwerem po TCP... (jeśli trwa to długo, to znaczy, że serwer jest niedostępny)");
            while (true) {
                if (isServerDisconnected) {
                    System.out.println("Próbuję ponownie połączyć się z serwerem po TCP... (jeśli trwa to długo, to znaczy, że serwer ciągle jest niedostępny)");
                    isServerDisconnected = false;
                }

                final Socket tcpSocket;
                final DatagramSocket udpClientSocket;

                if (healthCheck != null) {
                    healthCheck.interrupt();
                    healthCheck = null;
                }

                //try to connect with server by TCP
                try {
                    tcpSocket = new Socket(gameConfiguration.getTcpHost().getHostName(), gameConfiguration.getTcpHost().getPort());
                } catch (IOException e) {
                    //e.printStackTrace();
                    continue;
                }

                //wait for your gamer id on TCP
                String gamerId;
                byte[] packetWithGamerId = new byte[6];
                try {
                    System.out.println("Czekam na pakiet od serwera z moim id gracza po TCP");
                    while (true) {
                        len = tcpSocket.getInputStream().read(packetWithGamerId);
                        if (len != 6) {
                            System.out.println("Serwer wysłał niespodziewany pakiet, miał być pakiet z 5-znakowym id gracza <XID>");
                        } else {
                            break;
                        }
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
                            //System.out.println("Problem z uśpieniem wątku TCP wysyłającego pakiety aktywności");
                            continue;
                        }
                        synchronized (tcpSocket) {
                            try {
                                tcpSocket.getOutputStream().write("alive".getBytes());
                            } catch (IOException e) {
                                //e.printStackTrace();
                                //System.out.println("Problem z połączeniem TCP -> nieudane wysłanie po TCP pakietu aktywności");
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
                            while (true) {
                                len = tcpSocket.getInputStream().read(startPacket);
                                receivedPacket = convertToString(startPacket);
                                if (len != 5 || !receivedPacket.equals("start")) {
                                    //System.out.println("Serwer wysłał niespodziewany pakiet, miał być 'start'");
                                } else {
                                    System.out.println("Udane zainicjowanie UDP, otrzymany pakiet 'start' po TCP od serwera");
                                    isSuccess = true;
                                    break;
                                }
                            }
                            if (isSuccess) {
                                isSuccess = false;
                                break;
                            }
                        } catch (SocketTimeoutException e) {
                            isServerDisconnected = true;
                            System.out.println("Problem z połączeniem TCP podczas inicjalizacji UDP -> jeszcze nieobsłużone");
                            break;
                        }
                    }
                    if (isServerDisconnected) continue;
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
                            if (len == -1) {
                                isServerDisconnected = true;
                                System.out.println("Utracone połączenie z serwerem. Próba ponownego połączenia.");
                                break;
                            }
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

                        if (isServerDisconnected)  break;

                        System.out.println("Teraz zawieszam się w oczekiwaniu na start");
                        while (true) {
                            len = tcpSocket.getInputStream().read(startPacket);               //wait for start of game
                            if (len == -1) {
                                isServerDisconnected = true;
                                System.out.println("Utracone połączenie z serwerem. Próba ponownego połączenia.");
                                break;
                            }
                            receivedPacket = convertToString(startPacket);
                            if (len != 5 || !receivedPacket.equals("start")) {
                                //System.out.println("Niespodziewany pakiet zamiast pakietu 'start'");
                                continue;
                            }
                            System.out.println("Otrzymałem pakiet 'start' od serwera, wznawiam grę");
                            break;
                        }
                        if (isServerDisconnected) break;
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
