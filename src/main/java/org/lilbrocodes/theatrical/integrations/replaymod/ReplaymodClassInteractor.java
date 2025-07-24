package org.lilbrocodes.theatrical.integrations.replaymod;

import com.replaymod.recording.gui.GuiRecordingControls;
import com.replaymod.recording.ReplayModRecording;
import org.lilbrocodes.theatrical.mixin.accessor.GuiRecordingControlsDataHolder;
import org.lilbrocodes.theatrical.mixin.accessor.RecordingControlsAccessor;

public class ReplaymodClassInteractor {
    public static void startRecording() {
        getControls().theatrical$start();
    }

    public static RecordingControlsAccessor getControls() {
        GuiRecordingControlsDataHolder guiRecordingControlsDataHolder = (GuiRecordingControlsDataHolder) ReplayModRecording.instance.getConnectionEventHandler();
        GuiRecordingControls controls = guiRecordingControlsDataHolder.theatrical$getGuiControls();
        return (RecordingControlsAccessor) controls;
    }

    public static GuiRecordingControls rawControls() {
        GuiRecordingControlsDataHolder guiRecordingControlsDataHolder = (GuiRecordingControlsDataHolder) ReplayModRecording.instance.getConnectionEventHandler();
        return guiRecordingControlsDataHolder.theatrical$getGuiControls();
    }

    public static void stopRecording() {
        getControls().theatrical$stop();
    }

    public static boolean isRecording() {
        GuiRecordingControls controls = rawControls();
        if (controls == null) return false;
        return !controls.isStopped();
    }
}
