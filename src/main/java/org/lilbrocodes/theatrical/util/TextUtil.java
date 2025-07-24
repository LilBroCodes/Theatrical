package org.lilbrocodes.theatrical.util;

import net.minecraft.text.Text;

public class TextUtil {
    public static Text replaceTranslatable(String key, Object... replacements) {
        return Text.literal(String.format(Text.translatable(key).getString(), replacements));
    }
}
