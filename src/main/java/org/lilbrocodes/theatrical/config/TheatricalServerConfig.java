package org.lilbrocodes.theatrical.config;

import me.fzzyhmstrs.fzzy_config.api.FileType;
import me.fzzyhmstrs.fzzy_config.api.SaveType;
import me.fzzyhmstrs.fzzy_config.config.Config;
import me.fzzyhmstrs.fzzy_config.config.ConfigSection;
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedInt;
import org.jetbrains.annotations.NotNull;
import org.lilbrocodes.theatrical.Theatrical;

public class TheatricalServerConfig extends Config {
    public TheatricalServerConfig() {
        super(Theatrical.identify("server_config"));
    }

    @Override
    public int defaultPermLevel() {
        return 4;
    }

    @Override
    public @NotNull FileType fileType() {
        return FileType.JSONC;
    }

    @Override
    public @NotNull SaveType saveType() {
        return SaveType.SEPARATE;
    }

    public PlotArmorSection plotArmor = new PlotArmorSection();

    @Prefix("The amount of players that can have their checks not pass when starting a countdown for that countdown to start successfully")
    public ValidatedInt tolerableUncheckedPlayers = new ValidatedInt(0, Integer.MAX_VALUE, 0);

    @Prefix("The amount of time to wait before giving up on trying to start a countdown")
    public ValidatedInt countdownTimeoutMinutes = new ValidatedInt(5, Integer.MAX_VALUE, 0);

    public static class PlotArmorSection extends ConfigSection {

        @Name("Positive - Minimum Health")
        public float positive_minHealth = 0.5F;

        @Name("Positive - Regen Speed")
        public float positive_regenAmount = 2.0F;

        @Name("Negative - Damage Multiplier")
        public float negative_damageMultiplier = 1.3F;

        @Name("Negative - Regen Multiplier")
        public float negative_regenMultiplier = 0.5F;
    }

    @Override
    public @NotNull String translationKey() {
        return "theatrical.server_config";
    }

    @Override
    public boolean hasTranslation() {
        return true;
    }
}
