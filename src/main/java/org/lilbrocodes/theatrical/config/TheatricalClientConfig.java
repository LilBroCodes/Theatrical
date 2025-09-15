package org.lilbrocodes.theatrical.config;

import me.fzzyhmstrs.fzzy_config.api.FileType;
import me.fzzyhmstrs.fzzy_config.api.SaveType;
import me.fzzyhmstrs.fzzy_config.config.Config;
import me.fzzyhmstrs.fzzy_config.config.ConfigSection;
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedInt;
import org.jetbrains.annotations.NotNull;
import org.lilbrocodes.theatrical.Theatrical;
import org.lilbrocodes.theatrical.util.BindsMode;

public class TheatricalClientConfig extends Config {
    public TheatricalClientConfig() {
        super(Theatrical.identify("client_config"));
    }

    public static final int CONFIG_VERSION = 2;

    @Prefix("Used for keeping track of what config version you have, and running some data fixes if needed. Changing it will currently reset your client config to default.")
    public int configVersion = CONFIG_VERSION;

    @Override
    public int defaultPermLevel() {
        return 0;
    }

    @Override
    public @NotNull FileType fileType() {
        return FileType.JSONC;
    }

    @Override
    public @NotNull SaveType saveType() {
        return SaveType.SEPARATE;
    }

    public VisualsSection visuals = new VisualsSection();
    public ControlsSection controls = new ControlsSection();
    public ChecksSection checks = new ChecksSection();

    public static class ChecksSection extends ConfigSection {
        public VoicechatSection voicechat = new VoicechatSection();
        public ReplaymodSection replaymod = new ReplaymodSection();

        public static class VoicechatSection extends ConfigSection {
            @Prefix("If enabled, upon starting a countdown - instead of reporting that audio is not being recorded, it starts recording automatically")
            public boolean autoStartRecording = false;

            @Prefix("If enabled, it will require that you are recording voicechat audio if a countdown gets started with you in it")
            public boolean checkForRecording = false;
        }

        public static class ReplaymodSection extends ConfigSection {
            @Prefix("If enabled, upon starting a countdown - instead of reporting that a replay is not being recorded, it starts recording automatically")
            public boolean autoStartRecording = false;

            @Prefix("If enabled, it will require that you are recording a replay if a countdown gets started with you in it")
            public boolean checkForRecording = false;
        }
    }

    public static class VisualsSection extends ConfigSection {

        @Name("Rainbow Effect on Duplicate Keybinds")
        public boolean rainbowDuplicateKeybinds = false;

        @Name("Rainbow Effect Speed")
        public ValidatedInt rainbowEffectSpeed = new ValidatedInt(3, 10, 1);

        @Name("Show Walk Speed Change Message")
        public boolean showWalkSpeedChangeMessage = true;
    }

    public static class ControlsSection extends ConfigSection {
        public ControlsSection() {
            super();
        }

        @Name("Allow Duplicate Keybinds")
        public BindsMode allowDuplicateKeybinds = BindsMode.MC_AND_TH;
    }

    @Override
    public @NotNull String translationKey() {
        return "theatrical.client_config";
    }

    @Override
    public boolean hasTranslation() {
        return true;
    }
}
