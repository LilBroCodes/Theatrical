package org.lilbrocodes.theatrical.config;

import eu.midnightdust.lib.config.MidnightConfig;
import org.lilbrocodes.theatrical.util.BindsMode;

public class TheatricalConfig extends MidnightConfig {

    public static final String CONTROLS = "controls";
    public static final String VISUALS = "visuals";

    @Entry(category = CONTROLS) public static BindsMode allowDuplicateKeybinds = BindsMode.MC_AND_TH;

    @Entry(category = VISUALS) public static boolean rainbowDuplicateKeybinds = false;
    @Entry(category = VISUALS, isSlider = true, min = 1, max = 10) public static int rainbowEffectSpeed = 3;
    @Entry(category = VISUALS) public static boolean showWalkSpeedChangeMessage = true;
}
