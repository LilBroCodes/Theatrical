package org.lilbrocodes.theatrical.integrations.voicechat;

import org.lilbrocodes.theatrical.config.Configs;

public class VoicechatChecks {
    private static final boolean voicechatLoaded = VoicechatInteractor.voicechatLoaded();

    public static int runChecks() {
        if (!voicechatLoaded) return 0;
        if (!Configs.CLIENT.checks.voicechat.checkForRecording) return 4;

        if (!VoicechatInteractor.isRecording()) {
            if (Configs.CLIENT.checks.voicechat.autoStartRecording) {
                return 2;
            }
            return 3;
        }

        return 1;
    }

    public static void fixChecks() {
        if (!VoicechatInteractor.isRecording()) {
            if (Configs.CLIENT.checks.voicechat.autoStartRecording) {
                VoicechatInteractor.startOrRestartRecording();
            }
        }
    }
}
