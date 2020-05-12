package client.interfaces;

import client.model.network.GameConfiguration;

public interface ConfigurationWindowListener {

    void configurationWindowChanged(GameConfiguration gameConfiguration, Object source);
}
