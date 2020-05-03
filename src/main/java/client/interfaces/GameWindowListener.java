package client.interfaces;

import client.model.GameConfiguration;
import client.view.ConfigurationWindow;
import client.view.GameWindow;

public interface GameWindowListener {

    void gameWindowChanged(GameWindow gameWindow, Object source);
}
