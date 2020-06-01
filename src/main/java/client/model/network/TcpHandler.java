package client.model.network;

import client.controller.Controller;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.TimeUnit;

import static client.model.helpers.Helper.*;

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

            System.out.println("Próbuję połączyć się z serwerem po TCP... (jeśli trwa to długo, to znaczy, że serwer jest niedostępny)");
            mainLoop:
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
                int gamerId;
                byte[] packetWithGamerId = new byte[2];
                try {
                    System.out.println("Czekam na pakiet od serwera z moim id gracza po TCP");
                    while (true) {
                        len = tcpSocket.getInputStream().read(packetWithGamerId);
                        if (len != 2) {
                            System.out.println("Serwer wysłał niespodziewany pakiet, miał być pakiet z id gracza <XID>");
                        } else {
                            break;
                        }
                    }
                    gamerId = getNumberFromBuffer(packetWithGamerId, 1, 1);
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
                    continue mainLoop;
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
                    sendingstartedUdpPacketAndWaitingForStartLoop:
                    while (true) {
                        udpClientSocket.send(startUdpPacket);
                        try {
                            while (true) {
                                len = tcpSocket.getInputStream().read(startPacket);
                                //receivedPacket = convertToString(startPacket);
                                if (len != 5 || !ifStartPacket(startPacket)) {
                                    System.out.println("Serwer wysłał niespodziewany pakiet, miał być <S...>");
                                } else {
                                    System.out.println("Udane zainicjowanie UDP, otrzymany pakiet START po TCP od serwera");
                                    break sendingstartedUdpPacketAndWaitingForStartLoop;
                                }
                            }
                        } catch (SocketTimeoutException e) {
                            isServerDisconnected = true;
                            System.out.println("Problem z połączeniem TCP podczas inicjalizacji UDP -> jeszcze nieobsłużone");
                            continue mainLoop;
                        }
                    }
                } catch (IOException e) {
                    //e.printStackTrace();
                    System.out.println("Problem z wysłaniem pakietu startowego po UDP (lub ustawieniem timeout na TCP na 1 sek)");
                    continue mainLoop;
                }

                try {
                    tcpSocket.setSoTimeout(0);
                } catch (SocketException e) {
                    //e.printStackTrace();
                    System.out.println("Problem z ustawieniem domyślnego timeout na TCP");
                    continue mainLoop;
                }

                try (UdpWorker udpWorker = new UdpWorker(controller)) {
                    byte[] stopPacket = new byte[2];
                    byte[] startPacket = new byte[5];
                    while (true) {
                        udpWorker.execute();                //start of game
                        while (true) {
                            len = tcpSocket.getInputStream().read(stopPacket);               //wait for stop game
                            if (len == -1) {
                                isServerDisconnected = true;
                                System.out.println("Utracone połączenie z serwerem. Próba ponownego połączenia. (waiting for stop)");
                                break;
                            }
                            //receivedPacket = convertToString(stopPacket);
                            if (len != 2 || ifStopPacket(stopPacket)) {
                                System.out.println("Niespodziewany pakiet po TCP zamiast pakietu STOP");
                                continue;
                            }
                            System.out.println("Otrzymałem pakiet STOP, koniec rozgrywki");
                            break;
                        }

                        udpWorker.close();

                        //TODO: add event for GUI-> end of game
                        //java.awt.Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(null);

                        if (isServerDisconnected)  continue mainLoop;

                        System.out.println("Teraz zawieszam się w oczekiwaniu na START");
                        while (true) {
                            len = tcpSocket.getInputStream().read(startPacket);               //wait for start of game
                            if (len == -1) {
                                isServerDisconnected = true;
                                System.out.println("Utracone połączenie z serwerem. Próba ponownego połączenia. (waiting for start)");
                                continue mainLoop;
                            }
                            //receivedPacket = convertToString(startPacket);
                            if (len != 5 || ifStartPacket(startPacket)) {
                                System.out.println("Niespodziewany pakiet zamiast pakietu START");
                                continue;
                            }
                            System.out.println("Otrzymałem pakiet START od serwera, rozpoczynam nową rozgrywkę");
                            break;
                        }
                        if (isServerDisconnected) continue mainLoop;
                    }
                } catch (IOException ex) {
                    //ex.printStackTrace();
                    System.out.println("Problem z połączeniem TCP");
                    System.out.println("Kończę rozgrywkę");
                    continue mainLoop;
                }
            }
        };

        tcpHandler = new Thread(runnable);
        tcpHandler.start();                     //start thread responsible for handling TCP connection
    }
}
