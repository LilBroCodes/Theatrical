package org.lilbrocodes.theatrical.util;

import me.fzzyhmstrs.fzzy_config.util.EnumTranslatable;
import org.jetbrains.annotations.NotNull;

public enum BindsMode implements EnumTranslatable {
    NONE, MC_AND_TH, ALL;

    @Override
    public @NotNull String prefix() {
        return "theatrical.binds_mode";
    }
}