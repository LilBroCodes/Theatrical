package org.lilbrocodes.theatrical.integrations.voicechat;

import net.fabricmc.loader.api.FabricLoader;

public class VoicechatInteractor {
    public static boolean voicechatLoaded() {
        return FabricLoader.getInstance().isModLoaded("voicechat");
    }

    public static void startOrRestartRecording() {
        if (!voicechatLoaded()) return;

        if (VoicechatClassInteractor.isRecording()) VoicechatClassInteractor.toggleRecording();
        VoicechatClassInteractor.toggleRecording();
    }

    public static boolean isRecording() {
        return voicechatLoaded() && VoicechatClassInteractor.isRecording();
    }
}
