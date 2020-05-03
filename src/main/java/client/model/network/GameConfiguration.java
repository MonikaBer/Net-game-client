package client.model.network;

import java.net.InetSocketAddress;

public class GameConfiguration {

    private final InetSocketAddress tcpHost;
    private final InetSocketAddress udpHost;


    public GameConfiguration(InetSocketAddress tcpHost, InetSocketAddress udpHost) {

        this.tcpHost = tcpHost;
        this.udpHost = udpHost;
    }

    public InetSocketAddress getTcpHost() {
        return tcpHost;
    }

    public InetSocketAddress getUdpHost() {
        return udpHost;
    }
}
