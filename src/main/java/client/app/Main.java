package client.app;

import client.model.Model;

import javax.swing.*;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Main {

    public static void main(String[] args) {

        Runnable program = new Runnable() {
            @Override
            public void run() {

                Model model = new Model("localhost", 8000, 7000);

//                View view = new View();
//                Controller controller = new Controller(view, model);
//
//                view.addListener(controller);
            }
        };
        SwingUtilities.invokeLater(program);

    }
}