package client.view;

import client.interfaces.ConfigurationWindowListener;
import client.model.network.GameConfiguration;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetSocketAddress;

public class ConfigurationWindow extends JFrame implements ActionListener {

    private final JLabel lHost;
    private final JLabel lTcpPort;
    private final JLabel lUdpPort;
    private final JTextField tHost;
    private final JTextField tTcpPort;
    private final JTextField tUdpPort;
    private final JButton bOk;
    private ConfigurationWindowListener configurationWindowListener;

    public ConfigurationWindow() {

        this.setSize(500, 250);
        this.setResizable(false);
        this.setTitle("Konfiguracja");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(null);
        this.setVisible(true);

        lHost = new JLabel("Host");
        lHost.setBounds(20, 30, 120, 20);
        add(lHost);

        lTcpPort = new JLabel("UDP port");
        lTcpPort.setBounds(20, 60, 120, 20);
        add(lTcpPort);

        lUdpPort = new JLabel("TCP port");
        lUdpPort.setBounds(20, 90, 120, 20);
        add(lUdpPort);

        tHost = new JTextField("localhost");
        tHost.setBounds(190, 30, 200, 20);
        add(tHost);

        tTcpPort = new JTextField("8888");
        tTcpPort.setBounds(190, 60, 200, 20);
        add(tTcpPort);

        tUdpPort = new JTextField("8888");
        tUdpPort.setBounds(190, 90, 200, 20);
        add(tUdpPort);

        bOk = new JButton("Ok");
        bOk.setBounds(20, 120, 120, 20);
        add(bOk);
        bOk.addActionListener(this);
    }

    public void addListener(ConfigurationWindowListener configurationWindowListener) {
        this.configurationWindowListener = configurationWindowListener;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        String sHost = tHost.getText();
        String sTcpPort = tTcpPort.getText();
        String sUdpPort = tUdpPort.getText();

        try {
            InetSocketAddress tcpHost = new InetSocketAddress(sHost, Integer.parseInt(sTcpPort));
            InetSocketAddress udpHost = new InetSocketAddress(sHost, Integer.parseInt(sUdpPort));
            GameConfiguration gameConfiguration = new GameConfiguration(tcpHost, udpHost);
            this.configurationWindowListener.configurationWindowChanged(gameConfiguration, e.getSource());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Błędne dane", "Uwaga", JOptionPane.WARNING_MESSAGE);
        }
    }
}
