package org.lilbrocodes.theatrical.config;

import me.fzzyhmstrs.fzzy_config.api.ConfigApiJava;
import me.fzzyhmstrs.fzzy_config.api.RegisterType;

public class Configs {
    public static TheatricalClientConfig CLIENT;
    public static TheatricalServerConfig SERVER;

    public static void initialize() {
        CLIENT = ConfigApiJava.registerAndLoadConfig(TheatricalClientConfig::new, RegisterType.CLIENT);
        SERVER = ConfigApiJava.registerAndLoadConfig(TheatricalServerConfig::new, RegisterType.BOTH);

        if (CLIENT.configVersion != TheatricalClientConfig.CONFIG_VERSION) {
            CLIENT = new TheatricalClientConfig();
            CLIENT.configVersion = TheatricalClientConfig.CONFIG_VERSION;
            CLIENT.save();
        }
    }
}
