package client.model;

import client.model.cryptography.Cryptography;

import java.io.*;
import java.net.*;
import java.rmi.server.RemoteCall;
import java.util.Arrays;

public class Model {

    private Socket tcpSocket;
    private DatagramSocket udpClientSocket;
    private DatagramSocket udpServerSocket;
    private InetSocketAddress udpServerAddress;
    private String clientAddress;
    private int udpClientPort;
    private InputStream in;
    private OutputStream out;
    private byte[] buff;
    private String serverPublicKey;
    //private BufferedReader in;
    //private PrintWriter out;
    private DatagramPacket packet;


    public Model(String serverAddress, int tcpServerPort, int udpServerPort) throws Error {

        try {

            tcpSocket = new Socket(serverAddress, tcpServerPort);
            //in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            //out = new PrintWriter(socket.getOutputStream(), true);
            in = tcpSocket.getInputStream();
            out = tcpSocket.getOutputStream();

            buff = new byte[50];
            int len;

            while(true){                                     //oczekiwanie na potwierdzenie zestawienia połączenia tcp
                len = in.read(buff);
                if (len == -1) break;   //błąd

                if (buff[0] == 0) {
                    System.out.println("Połączenie TCP z serwerem zestawione");
                    break;
                }
            }

            //negocjacja kluczy
            Cryptography cryptography = new Cryptography();
            out.write(cryptography.getPublicKey().getBytes());        //wysłanie klucza publicznego klienta do serwera

            while (true) {                                            //oczekiwanie na klucz publiczny serwera
                len = in.read(buff);
                if (len == -1) break;   //błąd

                if (len == 10) {                                      //odebrano klucz publiczny serwera
                    StringBuilder sBuilder = new StringBuilder();
                    for(int i = 0; i < len; i++) {
                        sBuilder.append((char)buff[i]);
                    }

                    serverPublicKey = sBuilder.toString();
                    System.out.println(serverPublicKey);
                    break;
                }
            }

            out.write(cryptography.generateSessionKey().getBytes());        //wysłanie klucza sesji do serwera

            //utworzenie gniazda udp i wątku go obsługującego
            this.clientAddress = new String("127.0.0.1");
            this.udpClientPort = 1111;
            InetSocketAddress udpClientAddress = new InetSocketAddress(clientAddress, udpClientPort);
            udpClientSocket = new DatagramSocket(udpClientAddress);

            udpServerAddress = new InetSocketAddress(serverAddress, udpServerPort);
            StringBuilder sBuilder = new StringBuilder();
            sBuilder.append(this.clientAddress);
            sBuilder.append(":");
            sBuilder.append(this.udpClientPort);
            packet = new DatagramPacket(sBuilder.toString().getBytes(), 14, udpServerAddress);
            udpClientSocket.send(packet);



            //System.out.write(buff, 0, len);
            System.out.println("Hura");

            tcpSocket.close();


//            InetAddress serverAddress;
//            serverAddress = new InetSocketAddress("localhost", serverPort);
//            socket.connect(serverAddress);
            //System.out.println("Połączenie nawiązane");

        } catch (Exception e) {
            throw new Error("Połączenie odrzucone");
        }
    }
}
