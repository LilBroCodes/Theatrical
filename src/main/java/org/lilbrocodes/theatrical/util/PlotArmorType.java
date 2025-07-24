package org.lilbrocodes.theatrical.util;

import net.minecraft.util.StringIdentifiable;

public enum PlotArmorType implements StringIdentifiable {
    POSITIVE("positive"),
    NEGATIVE("negative"),
    NONE("none");

    public static final com.mojang.serialization.Codec<PlotArmorType> CODEC = StringIdentifiable.createCodec(PlotArmorType::values);
    private final String string;

    PlotArmorType(String name) {
        this.string = name;
    }

    @Override
    public String asString() {
        return string;
    }
}
