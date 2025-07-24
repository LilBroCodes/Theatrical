package org.lilbrocodes.theatrical.config;

import me.fzzyhmstrs.fzzy_config.api.ConfigApiJava;
import me.fzzyhmstrs.fzzy_config.api.RegisterType;

public class Configs {
    public static TheatricalClientConfig CLIENT = ConfigApiJava.registerAndLoadConfig(TheatricalClientConfig::new, RegisterType.CLIENT);
    public static TheatricalServerConfig SERVER = ConfigApiJava.registerAndLoadConfig(TheatricalServerConfig::new, RegisterType.BOTH);

    public static void initialize() {

    }
}
