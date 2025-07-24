package org.lilbrocodes.theatrical.integrations.replaymod;

import net.fabricmc.loader.api.FabricLoader;

public class ReplaymodInteractor {
    private static final boolean replayModLoaded;

    static {
        replayModLoaded = FabricLoader.getInstance().isModLoaded("replaymod");
    }

    public static boolean isReplayModLoaded() {
        return replayModLoaded;
    }

    public static void startRecording() {
        if (replayModLoaded) {
            ReplaymodClassInteractor.startRecording();
        }
    }

    public static void stopRecording() {
        if (replayModLoaded) {
            ReplaymodClassInteractor.stopRecording();
        }
    }

    public static void startOrRestartRecording() {
        if (isRecording()) stopRecording();
        startRecording();
    }

    public static boolean isRecording() {
        return replayModLoaded && ReplaymodClassInteractor.isRecording();
    }
}
