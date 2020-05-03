package client.interfaces;

import client.model.GameConfiguration;
import client.view.ConfigurationWindow;

public interface ConfigurationWindowListener {

    void configurationWindowChanged(GameConfiguration gameConfiguration, Object source);
}
