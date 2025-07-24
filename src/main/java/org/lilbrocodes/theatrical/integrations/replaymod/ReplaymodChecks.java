package org.lilbrocodes.theatrical.integrations.replaymod;

import org.lilbrocodes.theatrical.config.Configs;

public class ReplaymodChecks {
    private static final boolean replaymodLoaded = ReplaymodInteractor.isReplayModLoaded();

    public static int runChecks() {
        if (!replaymodLoaded) return 0;
        if (!Configs.CLIENT.checks.replaymod.checkForRecording) return 4;

        if (!ReplaymodInteractor.isRecording()) {
            if (Configs.CLIENT.checks.replaymod.autoStartRecording) {
                return 2;
            }
            return 3;
        }

        return 1;
    }

    public static void fixChecks() {
        if (!ReplaymodInteractor.isRecording()) {
            if (Configs.CLIENT.checks.replaymod.autoStartRecording) {
                ReplaymodInteractor.startOrRestartRecording();
            }
        }
    }
}
