package org.lilbrocodes.theatrical.util;


import net.minecraft.client.option.KeyBinding;
import org.lilbrocodes.theatrical.config.TheatricalConfig;

import java.util.HashSet;
import java.util.Set;

public class DupeBinds {
    public static final Set<KeyBinding> MC_TH_BINDS = new HashSet<>();

    public static boolean bindAllowed(KeyBinding keyBinding) {
        if (keyBinding == null) return false;
        return switch (TheatricalConfig.allowDuplicateKeybinds) {
            case NONE -> false;
            case MC_AND_TH -> MC_TH_BINDS.contains(keyBinding);
            case ALL -> true;
        };
    }
}
