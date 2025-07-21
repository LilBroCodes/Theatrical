package org.lilbrocodes.theatrical.util;

import org.lilbrocodes.theatrical.config.TheatricalConfig;

import java.awt.*;

public class RainbowColor {
    private static float hue = 0.0f;
    private static final float saturation = 1.0f;
    private static final float brightness = 1.0f;
    private static final float step = 0.01f;
    private static int tick = 0;
    public static int currentColor = getNextColor();

    /**
     * Returns the next color in the rainbow as an ARGB integer.
     * Starts at black and fades into full color.
     */
    private static int getNextColor() {
        Color color = Color.getHSBColor(hue, saturation, brightness);
        int alpha = 0xFF << 24;
        int rgb = color.getRGB() & 0x00FFFFFF;
        int argb = alpha | rgb;
        hue += step;
        if (hue > 1.0f) {
            hue -= 1.0f;
        }
        return argb;
    }

    public static void stepColor() {
        if (tick >= 10 - TheatricalConfig.rainbowEffectSpeed) {
            tick = 0;
            currentColor = getNextColor();
        } else {
            tick++;
        }
    }
}
